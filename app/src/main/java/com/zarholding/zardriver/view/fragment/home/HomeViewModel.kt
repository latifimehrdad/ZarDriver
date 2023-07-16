package com.zarholding.zardriver.view.fragment.home

import androidx.lifecycle.viewModelScope
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.model.repository.TokenRepository
import com.zarholding.zardriver.model.repository.TripDriverRepository
import com.zarholding.zardriver.model.data.response.TripModel
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private var tripDriverRepository: TripDriverRepository
) : ZarViewModel() {

    val tripLiveData: SingleLiveEvent<TripModel> by lazy {
        SingleLiveEvent<TripModel>()
    }


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestStartTripDriver() {
        viewModelScope.launch(IO + exceptionHandler()) {
            val response = callApi(tripDriverRepository.requestStartTripDriver())
            response?.let { tripLiveData.postValue(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


}