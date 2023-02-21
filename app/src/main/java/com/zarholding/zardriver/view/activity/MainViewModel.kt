package com.zarholding.zardriver.view.activity

import android.content.SharedPreferences
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.tools.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ZarViewModel() {


    //---------------------------------------------------------------------------------------------- deleteAllData
    fun deleteAllData() {
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, null)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- deleteAllData
}