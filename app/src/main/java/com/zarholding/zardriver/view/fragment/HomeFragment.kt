package com.zarholding.zardriver.view.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.InternetManager
import com.zarholding.zardriver.R
import com.zarholding.zardriver.background.TrackingService
import com.zarholding.zardriver.databinding.FragmentHomeBinding
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class HomeFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        var driving = false
        var timeSecond = 0L
    }

    @Inject
    lateinit var internetConnection: InternetManager

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (driving)
                Log.d("meri", "driving")
            else
                requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
        setStopDriving()
//        checkServiceIsRun()
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.imageViewDriving.setOnClickListener {
            if (driving)
                stopDriving()
            else
                startDriving()
        }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- startDriving
    private fun startDriving() {
        if (activity == null)
            return

        if (!checkLocationEnable()) {
            Toast.makeText(context, getString(R.string.errorLocationIsOff), Toast.LENGTH_LONG).show()
            return
        }


        requireActivity().startForegroundService(
            Intent(
                requireActivity(),
                TrackingService::class.java
            )
        )
        checkServiceIsRun()
    }
    //---------------------------------------------------------------------------------------------- startDriving


    //---------------------------------------------------------------------------------------------- stopDriving
    private fun stopDriving() {
        if (activity == null)
            return
        driving = false
        requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        checkServiceIsRun()
    }
    //---------------------------------------------------------------------------------------------- stopDriving


    //---------------------------------------------------------------------------------------------- checkServiceIsRun
    private fun checkServiceIsRun() {
        CoroutineScope(Main).launch {
            delay(1000)
            if (driving) setDriving()
            else setStopDriving()
        }
    }
    //---------------------------------------------------------------------------------------------- checkServiceIsRun


    //---------------------------------------------------------------------------------------------- setDriving
    private fun setDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_connect_button)
        binding.viewConnect1.visibility = View.VISIBLE
        binding.viewConnect2.visibility = View.VISIBLE
        binding.textViewDriving.text = getString(R.string.stopDriving)
        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.start()
        hideSystemUI()
    }
    //---------------------------------------------------------------------------------------------- setDriving


    //---------------------------------------------------------------------------------------------- setStopDriving
    private fun setStopDriving() {
        binding.imageViewDriving.setBackgroundResource(R.drawable.back_disconnect_button)
        binding.viewConnect1.visibility = View.INVISIBLE
        binding.viewConnect2.visibility = View.INVISIBLE
        binding.textViewDriving.text = getString(R.string.startDriving)
        binding.chronometer.stop()
        showSystemUI()
    }
    //---------------------------------------------------------------------------------------------- setStopDriving


    //---------------------------------------------------------------------------------------------- checkInternetConnected
    private fun checkInternetConnected() : Boolean {
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
    private fun checkLocationEnable() : Boolean {
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





    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        if (driving)
            requireActivity().stopService(Intent(requireActivity(), TrackingService::class.java))
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


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

}