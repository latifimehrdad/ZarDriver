package com.zarholding.zardriver.view.fragment.splash

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.viewModelScope
import com.zar.core.tools.manager.DeviceManager
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.model.data.response.AppVersionModel
import com.zarholding.zardriver.model.repository.DriverRepository
import com.zarholding.zardriver.model.repository.TokenRepository
import com.zarholding.zardriver.model.data.response.DriverModel
import com.zarholding.zardriver.model.repository.AppUpdateRepository
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val repository: DriverRepository,
    private val appUpdateRepository: AppUpdateRepository,
    private val deviceManager: DeviceManager
) : ZarViewModel() {

    val driverLiveData: SingleLiveEvent<DriverModel> by lazy {
        SingleLiveEvent<DriverModel>()
    }
    val userIsEnteredLiveData = SingleLiveEvent<Boolean>()
    val downloadVersionLiveData = SingleLiveEvent<String>()


    //---------------------------------------------------------------------------------------------- requestGetAppVersion
    fun requestGetAppVersion(appName: String) {
        viewModelScope.launch(IO + exceptionHandler()) {
            val response = callApi(
                response = appUpdateRepository.requestGetAppVersion(appName)
            )
            response?.let {
                checkAppVersion(it)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetAppVersion


    //---------------------------------------------------------------------------------------------- checkAppVersion
    private fun checkAppVersion(appVersionModel: AppVersionModel) {
        val currentVersion = deviceManager.appVersionCode()
        if (currentVersion < appVersionModel.currentVersion) {
            appVersionModel.fileName?.let {
                downloadVersionLiveData.postValue(it)
            }
        } else
            userIsEntered()
    }
    //---------------------------------------------------------------------------------------------- checkAppVersion


    //---------------------------------------------------------------------------------------------- userIsEntered
    private fun userIsEntered() {
        userIsEnteredLiveData.postValue(tokenRepository.userIsEntered())
    }
    //---------------------------------------------------------------------------------------------- userIsEntered



    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestGetDriverInfo() {
        viewModelScope.launch(IO + exceptionHandler()){
            val response = callApi(repository.requestGetDriverInfo())
            response?.let {  driverLiveData.postValue(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


    //---------------------------------------------------------------------------------------------- getInternalMemoryFreeSize
    fun getInternalMemoryFreeSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val bytesAvailable: Long = stat.blockSizeLong * stat.availableBlocksLong
        return bytesAvailable / (1024 * 1024)
    }
    //---------------------------------------------------------------------------------------------- getInternalMemoryFreeSize


}