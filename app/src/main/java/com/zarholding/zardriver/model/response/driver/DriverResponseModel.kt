package com.zarholding.zardriver.model.response.driver

import com.zarholding.zardriver.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/23/2022.
 */

data class DriverResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : DriverModel?
) : BaseResponseAbstractModel()