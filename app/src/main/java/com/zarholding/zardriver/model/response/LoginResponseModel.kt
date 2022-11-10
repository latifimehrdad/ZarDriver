package com.zarholding.zardriver.model.response

import com.zarholding.zardriver.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/9/2022.
 */

data class LoginResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data: String
) : BaseResponseAbstractModel()
