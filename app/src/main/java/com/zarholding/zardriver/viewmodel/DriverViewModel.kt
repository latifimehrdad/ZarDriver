package com.zarholding.zardriver.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zardriver.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/23/2022.
 */

@HiltViewModel
class DriverViewModel @Inject constructor(private val repository: DriverRepository) : ViewModel(){

    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestGetDriverInfo(token: String) = repository.requestGetDriverInfo(token)
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
}