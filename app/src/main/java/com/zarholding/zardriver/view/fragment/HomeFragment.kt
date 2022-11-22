package com.zarholding.zardriver.view.fragment

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.InternetManager
import com.zarholding.zardriver.R
import com.zarholding.zardriver.background.TrackingService
import com.zarholding.zardriver.databinding.FragmentHomeBinding
import com.zarholding.zardriver.model.response.TripModel
import com.zarholding.zardriver.utility.CompanionValues
import com.zarholding.zardriver.utility.EnumTripStatus
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.viewmodel.TokenViewModel
import com.zarholding.zardriver.viewmodel.TripDriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class HomeFragment : Fragment(), RemoteErrorEmitter {

    companion object {
        var chronometerBase = 0L
        var tripStatus = EnumTripStatus.STOP
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private var timerCounting = false


    @Inject
    lateinit var internetConnection: InternetManager

    lateinit var tripModel : TripModel

    private val tripViewModel: TripDriverViewModel by viewModels()
    private val tokenViewModel : TokenViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.remoteErrorEmitter = this
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
        setStopDriving()
        checkServiceIsRun()
        checkInternetConnected()
        checkLocationEnable()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutHome, message, 10 * 1000)
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.chronometer.setOnChronometerTickListener { chronometerTick() }
        binding.textViewGPS.setOnClickListener { turnGPSOn() }
        binding.imageViewGps.setOnClickListener { turnGPSOn() }
        binding.textViewInternet.setOnClickListener { turnOnInternet() }
        binding.imageViewDriving.setOnClickListener { drivingClick() }
        binding.textViewLocation.setOnClickListener { turnGPSOn() }
        binding.imageViewInternet.setOnClickListener { turnOnInternet() }
        binding.textViewInternetSetting.setOnClickListener { turnOnInternet() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- drivingClick
    private fun drivingClick() {
        when (tripStatus) {
            EnumTripStatus.STOP -> beforeStartDriving()
            else -> stopDriving()
        }
    }
    //---------------------------------------------------------------------------------------------- drivingClick


    //---------------------------------------------------------------------------------------------- chronometerTick
    private fun chronometerTick() {
        if (tripStatus != EnumTripStatus.START)
            checkServiceIsRun()
    }
    //---------------------------------------------------------------------------------------------- chronometerTick


    //---------------------------------------------------------------------------------------------- beforeStartDriving
    private fun beforeStartDriving() {
        if (activity == null)
            return
        if (!checkLocationEnable()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.errorLocationIsOff))
            return
        }
        if (!checkInternetConnected()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.errorInternetIsOff))
            return
        }
        requestStartTripDriver()
    }
    //---------------------------------------------------------------------------------------------- beforeStartDriving


    //---------------------------------------------------------------------------------------------- requestStartTripDriver
    private fun requestStartTripDriver() {

        if (tripStatus == EnumTripStatus.STOP) {
            tripStatus = EnumTripStatus.WAITING
            setWaitingDriving()
            tripViewModel.requestStartTripDriver(tokenViewModel.getBearerToken())
                .observe(viewLifecycleOwner) { response ->
                response?.let {
                    if (it.hasError) {
                        onError(EnumErrorType.UNKNOWN, it.message)
                        tripStatus = EnumTripStatus.STOP
                        setStopDriving()
                    } else {
                        it.data?.let {dataResponse ->
                            tripModel = dataResponse
                            startDriving()
                        } ?: run {
                            tripStatus = EnumTripStatus.STOP
                            setStopDriving()
                        }
                    }
                } ?: run {
                    tripStatus = EnumTripStatus.STOP
                    setStopDriving()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestStartTripDriver


    //---------------------------------------------------------------------------------------------- startDriving
    private fun startDriving() {
        startChronometer()
        startServiceBackground()
    }
    //---------------------------------------------------------------------------------------------- startDriving


    //---------------------------------------------------------------------------------------------- startChronometer
    private fun startChronometer() {
        if (timerCounting)
            return
        timerCounting = true
        if (chronometerBase == 0L)
            chronometerBase = SystemClock.elapsedRealtime()
        binding.chronometer.base = chronometerBase
        binding.chronometer.start()
    }
    //---------------------------------------------------------------------------------------------- startChronometer


    //---------------------------------------------------------------------------------------------- startServiceBackground
    private fun startServiceBackground() {
        val serviceId = "service${tripModel.Id}"
        Log.i("meri", serviceId)
        val intent = Intent(requireActivity(), TrackingService::class.java)
        intent.putExtra(CompanionValues.spToken, tokenViewModel.getToken())
        intent.putExtra(CompanionValues.spServiceId, serviceId)
        requireActivity().startForegroundService(intent)
    }
    //---------------------------------------------------------------------------------------------- startServiceBackground


    //---------------------------------------------------------------------------------------------- stopDriving
    private fun stopDriving() {
        if (activity == null)
            return
        chronometerBase = 0L
        timerCounting = false
        binding.chronometer.stop()
        tripStatus = EnumTripStatus.STOP
        requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        checkServiceIsRun()
    }
    //---------------------------------------------------------------------------------------------- stopDriving


    //---------------------------------------------------------------------------------------------- checkServiceIsRun
    private fun checkServiceIsRun() {
        job = CoroutineScope(IO).launch {
            delay(1000)
            withContext(Main) {
                when (tripStatus) {
                    EnumTripStatus.WAITING, EnumTripStatus.CONNECTING -> setWaitingDriving()
                    EnumTripStatus.STOP -> setStopDriving()
                    EnumTripStatus.START -> setDriving()
                    EnumTripStatus.RECONNECT -> setReconnectDriving()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- checkServiceIsRun


    //---------------------------------------------------------------------------------------------- setDriving
    private fun setDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_connect_button)
        startAnimationConnected()
        binding.textViewDriving.text = getString(R.string.stopDriving)
        if (!binding.chronometer.isCountDown)
            startChronometer()
    }
    //---------------------------------------------------------------------------------------------- setDriving


    //---------------------------------------------------------------------------------------------- setWaitingDriving
    private fun setWaitingDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_waiting_button)
        stopAnimationConnected()
        binding.textViewDriving.text = getString(R.string.waitingDriving)
    }
    //---------------------------------------------------------------------------------------------- setWaitingDriving


    //---------------------------------------------------------------------------------------------- setReconnectDriving
    private fun setReconnectDriving() {
        requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_reconnect_button)
        stopAnimationConnected()
        binding.textViewDriving.text = getString(R.string.reconnectDriving)
        if (checkInternetConnected() && checkLocationEnable()) {
            tripStatus = EnumTripStatus.CONNECTING
            startDriving()
        }
    }
    //---------------------------------------------------------------------------------------------- setReconnectDriving


    //---------------------------------------------------------------------------------------------- setStopDriving
    private fun setStopDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_disconnect_button)
        stopAnimationConnected()
        binding.textViewDriving.text = getString(R.string.startDriving)
    }
    //---------------------------------------------------------------------------------------------- setStopDriving


    //---------------------------------------------------------------------------------------------- startAnimationConnected
    private fun startAnimationConnected() {
        val animation1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        binding.viewConnect1.animation = animation1
        binding.viewConnect2.animation = animation2
        binding.viewConnect1.visibility = View.VISIBLE
        binding.viewConnect2.visibility = View.VISIBLE
    }
    //---------------------------------------------------------------------------------------------- startAnimationConnected


    //---------------------------------------------------------------------------------------------- stopAnimationConnected
    private fun stopAnimationConnected() {
        binding.viewConnect1.animation = null
        binding.viewConnect2.animation = null
        binding.viewConnect1.visibility = View.INVISIBLE
        binding.viewConnect2.visibility = View.INVISIBLE
    }
    //---------------------------------------------------------------------------------------------- stopAnimationConnected


    //---------------------------------------------------------------------------------------------- checkInternetConnected
    private fun checkInternetConnected(): Boolean {
        return when (internetConnection.connection()) {
            com.zar.core.enums.EnumInternetConnection.WIFI,
            com.zar.core.enums.EnumInternetConnection.CELLULAR -> {
                setImageConnectionSrc(true, binding.imageViewInternet)
                true
            }
            else -> {
                setImageConnectionSrc(false, binding.imageViewInternet)
                false
            }
        }
    }
    //---------------------------------------------------------------------------------------------- checkInternetConnected


    //---------------------------------------------------------------------------------------------- checkLocationEnable
    private fun checkLocationEnable(): Boolean {
        val mLocationManager = requireContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setImageConnectionSrc(true, binding.imageViewGps)
            true
        } else {
            setImageConnectionSrc(false, binding.imageViewGps)
            false
        }
    }
    //---------------------------------------------------------------------------------------------- checkLocationEnable


    //---------------------------------------------------------------------------------------------- setImageConnectionSrc
    private fun setImageConnectionSrc(connect: Boolean, image: ImageView) {
        if (connect) {
            image.setImageResource(R.drawable.ic_connect)
            image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.connect
                ),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            image.setImageResource(R.drawable.ic_disconnect)
            image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.disconnect
                ),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }
    //---------------------------------------------------------------------------------------------- setImageConnectionSrc


    //---------------------------------------------------------------------------------------------- turnGPSOn
    private fun turnGPSOn() {
        if (checkLocationEnable())
            return
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    //---------------------------------------------------------------------------------------------- turnGPSOn


    //---------------------------------------------------------------------------------------------- turnOnInternet
    private fun turnOnInternet() {
        if (checkInternetConnected())
            return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        else
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
    }
    //---------------------------------------------------------------------------------------------- turnOnInternet


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}