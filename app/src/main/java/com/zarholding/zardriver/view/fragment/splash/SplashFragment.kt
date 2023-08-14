package com.zarholding.zardriver.view.fragment.splash

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.manager.PermissionManager
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarFragment
import com.zarholding.zardriver.databinding.FragmentSplashBinding
import com.zarholding.zardriver.model.data.enums.EnumSystemType
import com.zarholding.zardriver.model.data.response.DriverModel
import com.zarholding.zardriver.tools.CompanionValues
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.view.dialog.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class SplashFragment(override var layout: Int = R.layout.fragment_splash):
    ZarFragment<FragmentSplashBinding>(){

    private val splashViewModel: SplashViewModel by viewModels()

    private var job: Job? = null

    @Inject
    lateinit var permissionManager: PermissionManager

    private val requestLocation = 99
    private val requestBackgroundLocation = 66


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        checkLocationPermission()
        binding.btnLogin.setOnClickListener { checkLocationPermission() }
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        binding.textViewMessage.text = message
        binding.btnLogin.visibility = View.VISIBLE
        activity?.let { (it as MainActivity).showMessage(message) }
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- locationPermissionLauncher
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionManager.checkPermissionResult(results) {
                if (it)
                    requestBackgroundLocationPermission()
            }
        }
    //---------------------------------------------------------------------------------------------- locationPermissionLauncher



    //---------------------------------------------------------------------------------------------- checkLocationPermission
    private fun checkLocationPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        val check =permissionManager.isPermissionGranted(
            permissions = permission,
            launcher = locationPermissionLauncher
        )
        if (check)
            requestBackgroundLocationPermission()
    }
    //---------------------------------------------------------------------------------------------- checkLocationPermission



    //---------------------------------------------------------------------------------------------- backgroundLocationPermissionLauncher
    private val backgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionManager.checkPermissionResult(results) {
                if (it)
                    notificationPermission()
            }
        }
    //---------------------------------------------------------------------------------------------- backgroundLocationPermissionLauncher



    //---------------------------------------------------------------------------------------------- requestBackgroundLocationPermission
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val check = permissionManager.isPermissionGranted(
                permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                launcher = backgroundLocationPermissionLauncher
            )
            if (check)
                notificationPermission()
        } else {
            val check = permissionManager.isPermissionGranted(
                permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
                launcher = backgroundLocationPermissionLauncher
            )
            if (check)
                notificationPermission()
        }
    }
    //---------------------------------------------------------------------------------------------- requestBackgroundLocationPermission



    //---------------------------------------------------------------------------------------------- notificationPermissionLauncher
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionManager.checkPermissionResult(results) {
                if (it)
                    requestGetAppVersion()
            }
        }
    //---------------------------------------------------------------------------------------------- notificationPermissionLauncher



    //---------------------------------------------------------------------------------------------- notificationPermission
    private fun notificationPermission() {
        binding.textViewMessage.text = "در حال ورود به سیستم"
        binding.btnLogin.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = listOf(Manifest.permission.POST_NOTIFICATIONS)
            val check = permissionManager.isPermissionGranted(
                permissions = permission,
                launcher = notificationPermissionLauncher
            )
            if (check) requestGetAppVersion()
        } else requestGetAppVersion()
    }
    //---------------------------------------------------------------------------------------------- notificationPermission



    //---------------------------------------------------------------------------------------------- requestGetAppVersion
    private fun requestGetAppVersion() {
        splashViewModel.requestGetAppVersion(EnumSystemType.DriverApp.name)
    }
    //---------------------------------------------------------------------------------------------- requestGetAppVersion



    //---------------------------------------------------------------------------------------------- observeLiveData
    private fun observeLiveData() {
        splashViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }

        splashViewModel.userIsEnteredLiveData.observe(viewLifecycleOwner) {
            if (it)
                splashViewModel.requestGetDriverInfo()
            else
                gotoFragmentLogin()
        }


        splashViewModel.downloadVersionLiveData.observe(viewLifecycleOwner) {
            storagePermission(it)
        }


        splashViewModel.driverLiveData.observe(viewLifecycleOwner){
            gotoFragmentHome(it)
        }
    }
    //---------------------------------------------------------------------------------------------- observeLiveData



    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Main) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentLogin


    //---------------------------------------------------------------------------------------------- gotoFragmentHome
    private fun gotoFragmentHome(driverModel: DriverModel) {
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Main) {
                val bundle = Bundle()
                bundle.putParcelable(CompanionValues.driverModel, driverModel)
                findNavController().navigate(R.id.action_splashFragment_to_HomeFragment, bundle)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentHome


    //---------------------------------------------------------------------------------------------- storagePermissionLauncher
    private val storagePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionManager.checkPermissionResult(results) {
                if (it && splashViewModel.downloadVersionLiveData.value != null)
                    showDialogUpdateAppVersion(splashViewModel.downloadVersionLiveData.value!!)
            }
        }
    //---------------------------------------------------------------------------------------------- storagePermissionLauncher



    //---------------------------------------------------------------------------------------------- cameraPermission
    private fun storagePermission(fileName: String) {
        val availableBlocks = splashViewModel.getInternalMemoryFreeSize()
        if (availableBlocks < 200) {
            showMessage(getString(R.string.internalMemoryIsFull))
            return
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val permissions = listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val check = permissionManager.isPermissionGranted(
                permissions = permissions,
                launcher = storagePermissionLauncher
            )
            if (check)
                showDialogUpdateAppVersion(fileName)
        } else
            showDialogUpdateAppVersion(fileName)
    }
    //---------------------------------------------------------------------------------------------- cameraPermission



    //---------------------------------------------------------------------------------------------- showDialogUpdateAppVersion
    private fun showDialogUpdateAppVersion(fileName: String) {
        if (context == null)
            return
        ConfirmDialog(
            context = requireContext(),
            type = ConfirmDialog.ConfirmType.WARNING,
            title = getString(R.string.doYouWantToUpdateApp),
            onYesClick = { gotoFragmentDownload(fileName) },
            onShowDialog = {  },
            onDismissDialog = {  },
            force = true
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogUpdateAppVersion


    //---------------------------------------------------------------------------------------------- gotoFragmentDownload
    private fun gotoFragmentDownload(fileName: String) {
        val bundle = Bundle()
        bundle.putString(CompanionValues.DOWNLOAD_URL, fileName)
        bundle.putString(CompanionValues.APP_NAME, EnumSystemType.DriverApp.name)
        gotoFragment(R.id.action_goto_DownloadUpdateFragment, bundle)
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentDownload



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}