package com.zarholding.zardriver.model.data.response


/**
 * create by m-latifi on 4/5/2023
 */

data class GeneralResponse<T>(
    val hasError : Boolean,
    val message : String,
    val data: T?
)