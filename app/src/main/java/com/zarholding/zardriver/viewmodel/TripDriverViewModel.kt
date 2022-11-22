package com.zarholding.zardriver.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zardriver.repository.TripDriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/22/2022.
 */
@HiltViewModel
class TripDriverViewModel @Inject constructor(var repository: TripDriverRepository) : ViewModel() {

    //---------------------------------------------------------------------------------------------- requestStartTripDriver
    fun requestStartTripDriver(token: String) = repository.requestStartTripDriver(token)
    //---------------------------------------------------------------------------------------------- requestStartTripDriver
}