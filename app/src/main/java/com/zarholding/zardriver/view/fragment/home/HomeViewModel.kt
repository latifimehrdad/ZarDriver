package com.zarholding.zardriver.view.fragment.home

import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.di.ResourcesProvider
import com.zarholding.zardriver.model.repository.TokenRepository
import com.zarholding.zardriver.model.repository.TripDriverRepository
import com.zarholding.zardriver.model.response.TripModel
import com.zarholding.zardriver.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val resourcesProvider: ResourcesProvider,
    private var tripDriverRepository: TripDriverRepository
) : ZarViewModel() {

    val tripLiveData: SingleLiveEvent<TripModel> by lazy {
        SingleLiveEvent<TripModel>()
    }


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestStartTripDriver() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripDriverRepository.requestStartTripDriver()
            if (response?.isSuccessful == true) {
                val driver = response.body()
                driver?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { model ->
                            tripLiveData.postValue(model)
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.tripInfoIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.tripInfoIsEmpty))
                }
            } else
                setMessage(response)
        }

    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


}