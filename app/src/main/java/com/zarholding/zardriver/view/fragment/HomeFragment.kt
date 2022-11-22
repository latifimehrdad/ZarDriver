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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.InternetManager
import com.zarholding.zardriver.R
import com.zarholding.zardriver.background.TrackingService
import com.zarholding.zardriver.databinding.FragmentHomeBinding
import com.zarholding.zardriver.utility.EnumTripStatus
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


    @Inject
    lateinit var internetConnection: InternetManager


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

    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.imageViewDriving.setOnClickListener {
            when(tripStatus) {
                EnumTripStatus.STOP -> beforeStartDriving()
                else -> stopDriving()
            }
        }

        binding.textViewGPS.setOnClickListener {
            turnGPSOn()
        }

        binding.textViewLocation.setOnClickListener {
            turnGPSOn()
        }

        binding.imageViewGps.setOnClickListener {
            turnGPSOn()
        }

        binding.textViewInternet.setOnClickListener {
            turnOnInternet()
        }

        binding.textViewInternetSetting.setOnClickListener {
            turnOnInternet()
        }

        binding.imageViewInternet.setOnClickListener {
            turnOnInternet()
        }

        binding.chronometer.setOnChronometerTickListener {
            if (tripStatus != EnumTripStatus.START)
                checkServiceIsRun()
        }
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- beforeStartDriving
    private fun beforeStartDriving() {
        if (activity == null)
            return
        if (!checkLocationEnable()) {
            showWarningMessage(getString(R.string.errorLocationIsOff))
            return
        }
        if (!checkInternetConnected()) {
            showWarningMessage(getString(R.string.errorInternetIsOff))
            return
        }
        startDriving()
    }
    //---------------------------------------------------------------------------------------------- beforeStartDriving



    //---------------------------------------------------------------------------------------------- startDriving
    private fun startDriving() {
        Log.d("meri", "startDriving")
        hideSystemUI()
        startChronometer()
        startServiceBackground()
    }
    //---------------------------------------------------------------------------------------------- startDriving



    //---------------------------------------------------------------------------------------------- startChronometer
    private fun startChronometer() {
        if (chronometerBase == 0L)
            chronometerBase = SystemClock.elapsedRealtime()
        binding.chronometer.base = chronometerBase
        binding.chronometer.start()
    }
    //---------------------------------------------------------------------------------------------- startChronometer



    //---------------------------------------------------------------------------------------------- startServiceBackground
    private fun startServiceBackground() {
        requireActivity().startForegroundService(
            Intent(
                requireActivity(),
                TrackingService::class.java
            )
        )
    }
    //---------------------------------------------------------------------------------------------- startServiceBackground



    //---------------------------------------------------------------------------------------------- stopDriving
    private fun stopDriving() {
        if (activity == null)
            return
        binding.chronometer.stop()
        showSystemUI()
        tripStatus = EnumTripStatus.STOP
        requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        checkServiceIsRun()
    }
    //---------------------------------------------------------------------------------------------- stopDriving


    //---------------------------------------------------------------------------------------------- checkServiceIsRun
    private fun checkServiceIsRun() {
        Log.d("meri", "checkServiceIsRun")
        CoroutineScope(IO).launch {
            delay(500)
            Log.d("meri", "withContext")
            withContext(Main) {
                Log.d("meri", "withContext : $tripStatus")
                when(tripStatus) {
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
        binding.viewConnect1.visibility = View.VISIBLE
        binding.viewConnect2.visibility = View.VISIBLE
        binding.textViewDriving.text = getString(R.string.stopDriving)
    }
    //---------------------------------------------------------------------------------------------- setDriving



    //---------------------------------------------------------------------------------------------- setWaitingDriving
    private fun setWaitingDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_waiting_button)
        binding.viewConnect1.visibility = View.VISIBLE
        binding.viewConnect2.visibility = View.VISIBLE
        binding.textViewDriving.text = getString(R.string.waitingDriving)
    }
    //---------------------------------------------------------------------------------------------- setWaitingDriving




    //---------------------------------------------------------------------------------------------- setReconnectDriving
    private fun setReconnectDriving() {
        requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_reconnect_button)
        binding.viewConnect1.visibility = View.VISIBLE
        binding.viewConnect2.visibility = View.VISIBLE
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
        binding.viewConnect1.visibility = View.INVISIBLE
        binding.viewConnect2.visibility = View.INVISIBLE
        binding.textViewDriving.text = getString(R.string.startDriving)
    }
    //---------------------------------------------------------------------------------------------- setStopDriving


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


    //---------------------------------------------------------------------------------------------- hideSystemUI
    private fun hideSystemUI() {
        requireActivity().window.decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )
    }
    //---------------------------------------------------------------------------------------------- hideSystemUI


    //---------------------------------------------------------------------------------------------- showSystemUI
    private fun showSystemUI() {
        requireActivity().window.decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }
    //---------------------------------------------------------------------------------------------- showSystemUI


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



    //---------------------------------------------------------------------------------------------- showWarningMessage
    private fun showWarningMessage(message : String) {
        val snack = Snackbar.make(binding.constraintLayoutHome, message, 10*1000)
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- showWarningMessage



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}