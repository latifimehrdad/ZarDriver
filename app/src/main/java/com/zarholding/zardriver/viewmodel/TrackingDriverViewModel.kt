package com.zarholding.zardriver.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import com.zarholding.zardriver.repository.TrackDriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/12/2022.
 */

@HiltViewModel
class TrackingDriverViewModel @Inject constructor(var repository: TrackDriverRepository) : ViewModel() {

    //---------------------------------------------------------------------------------------------- requestTrackDriver
    fun requestTrackDriver(trackDriver: TrackDriverRequestModel) =
        repository.requestTrackDriver(trackDriver)
    //---------------------------------------------------------------------------------------------- requestTrackDriver

}