package com.zarholding.zardriver.view.fragment.splash

import androidx.lifecycle.viewModelScope
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.model.repository.DriverRepository
import com.zarholding.zardriver.model.repository.TokenRepository
import com.zarholding.zardriver.model.data.response.DriverModel
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val repository: DriverRepository
) : ZarViewModel() {

    val driverLiveData: SingleLiveEvent<DriverModel> by lazy {
        SingleLiveEvent<DriverModel>()
    }

    //---------------------------------------------------------------------------------------------- userIsEntered
    fun userIsEntered() = tokenRepository.userIsEntered()
    //---------------------------------------------------------------------------------------------- userIsEntered


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestGetDriverInfo() {
        viewModelScope.launch(IO + exceptionHandler()){
            val response = callApi(repository.requestGetDriverInfo())
            response?.let {  driverLiveData.postValue(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


}