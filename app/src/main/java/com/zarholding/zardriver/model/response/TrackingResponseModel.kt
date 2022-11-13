package com.zarholding.zardriver.model.response

/**
 * Created by m-latifi on 11/13/2022.
 */

data class TrackingResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : TrackingDataResponseModel

) : BaseResponseAbstractModel()
