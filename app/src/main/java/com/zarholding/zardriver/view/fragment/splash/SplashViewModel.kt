package com.zarholding.zardriver.view.fragment.splash

import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.di.ResourcesProvider
import com.zarholding.zardriver.model.repository.DriverRepository
import com.zarholding.zardriver.model.repository.TokenRepository
import com.zarholding.zardriver.model.response.driver.DriverModel
import com.zarholding.zardriver.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val repository: DriverRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    val driverLiveData: SingleLiveEvent<DriverModel> by lazy {
        SingleLiveEvent<DriverModel>()
    }

    //---------------------------------------------------------------------------------------------- userIsEntered
    fun userIsEntered() = tokenRepository.userIsEntered()
    //---------------------------------------------------------------------------------------------- userIsEntered


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestGetDriverInfo() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = repository.requestGetDriverInfo()
            if (response?.isSuccessful == true) {
                val driver = response.body()
                driver?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { model ->
                            driverLiveData.postValue(model)
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.driverInfoIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.driverInfoIsEmpty))
                }
            } else
                setMessage(response)
        }

    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


}