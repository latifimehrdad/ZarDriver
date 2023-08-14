package com.zarholding.zardriver

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zardriver.di.ResourcesProvider
import com.zarholding.zardriver.model.data.response.GeneralResponse
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by m-latifi on 10/8/2022.
 */

@HiltViewModel
open class ZarViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var resourcesProvider: ResourcesProvider
    val errorLiveDate: SingleLiveEvent<ErrorApiModel> by lazy { SingleLiveEvent<ErrorApiModel>() }


    //---------------------------------------------------------------------------------------------- callApi
    suspend fun <T : Any> callApi(
        response: Response<GeneralResponse<T?>>?,
        showMessageAfterSuccessResponse: Boolean = false
    ): T? {
        if (response?.isSuccessful == true) {
            val body = response.body()
            body?.let { generalResponse ->
                if (generalResponse.hasError || generalResponse.data == null)
                    setMessage(generalResponse.message)
                else {
                    if (showMessageAfterSuccessResponse)
                        setMessage(generalResponse.message)
                    return generalResponse.data
                }
            } ?: run {
                setMessage(
                    resourcesProvider.getString(
                        R.string.dataReceivedIsEmpty
                    )
                )
            }
        } else setMessage(response)
        return null
    }
    //---------------------------------------------------------------------------------------------- callApi


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setMessage(response: Response<*>?) {
        withContext(Main) {
            errorLiveDate.postValue(checkResponseError(response))
        }
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- setMessage
    private fun setMessage(message: String) {
        errorLiveDate.postValue(ErrorApiModel(EnumApiError.Error, message))
    }
    //---------------------------------------------------------------------------------------------- setMessage



    //---------------------------------------------------------------------------------------------- exceptionHandler
    fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        throwable.localizedMessage?.let { setMessage(it) }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


}