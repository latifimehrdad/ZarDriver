package com.zarholding.zardriver.model.response

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : TripModel?
    )
    :BaseResponseAbstractModel()