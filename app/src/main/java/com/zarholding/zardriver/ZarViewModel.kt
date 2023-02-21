package com.zarholding.zardriver

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by m-latifi on 10/8/2022.
 */

@HiltViewModel
open class ZarViewModel @Inject constructor() : ViewModel() {

    var job: Job? = null
    val errorLiveDate : SingleLiveEvent<ErrorApiModel> by lazy { SingleLiveEvent<ErrorApiModel>() }


    //---------------------------------------------------------------------------------------------- setError
    fun setMessage(response: Response<*>?) {
        checkResponseError(response, errorLiveDate)
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- setMessage
    fun setMessage(message : String) {
        errorLiveDate.postValue(ErrorApiModel(EnumApiError.Error, message))
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setMessage


    //---------------------------------------------------------------------------------------------- exceptionHandler
    fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        throwable.localizedMessage?.let { setMessage(it) }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared

}