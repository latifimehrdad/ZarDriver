package com.zarholding.zardriver.view.fragment.home

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.zar.core.tools.manager.InternetManager
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarFragment
import com.zarholding.zardriver.background.TrackingService
import com.zarholding.zardriver.databinding.FragmentHomeBinding
import com.zarholding.zardriver.model.response.TripModel
import com.zarholding.zardriver.model.response.driver.DriverModel
import com.zarholding.zardriver.utility.CompanionValues
import com.zarholding.zardriver.utility.EnumTripStatus
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class HomeFragment(override var layout: Int = R.layout.fragment_home)
    : ZarFragment<FragmentHomeBinding>() {

    companion object {
        var chronometerBase = 0L
        var tripStatus = EnumTripStatus.STOP
    }

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var internetConnection: InternetManager

    private lateinit var tripModel : TripModel
    private var job: Job? = null
    private var timerCounting = false
    private var driverModel : DriverModel? = null



    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        driverModel = arguments?.getParcelable(CompanionValues.driverModel)
        observeLiveDate()
        setListener()
        setStopDriving()
        checkServiceIsRun()
        checkInternetConnected()
        checkLocationEnable()
/*        val start = Location(LocationManager.GPS_PROVIDER)
        start.latitude = 35.840267
        start.longitude = 51.017524
        val end = Location(LocationManager.GPS_PROVIDER)
        end.latitude = 35.840404
        end.longitude = 51.015378
        Log.i("meri", "measure distance = ${measureDistance(start, end)}")*/
    }
    //---------------------------------------------------------------------------------------------- onViewCreated



    //---------------------------------------------------------------------------------------------- observeLiveDate
    private fun observeLiveDate() {
        homeViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            setStopDriving()
            showMessage(it.message)
        }

        homeViewModel.tripLiveData.observe(viewLifecycleOwner){
            tripModel = it
            startDriving()
        }

    }
    //---------------------------------------------------------------------------------------------- observeLiveDate




    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let { (it as MainActivity).showMessage(message) }
    }
    //---------------------------------------------------------------------------------------------- showMessage



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
            showMessage(getString(R.string.errorLocationIsOff))
            return
        }
        if (!checkInternetConnected()) {
            showMessage(getString(R.string.errorInternetIsOff))
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
            homeViewModel.requestStartTripDriver()
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
        val intent = Intent(requireActivity(), TrackingService::class.java)
        intent.putExtra(CompanionValues.TOKEN, homeViewModel.getToken())
        intent.putExtra(CompanionValues.tripModel, tripModel)
        intent.putExtra(CompanionValues.driverId, driverModel?.id)
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
        tripStatus = EnumTripStatus.STOP
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
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}