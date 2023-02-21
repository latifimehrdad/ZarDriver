package com.zarholding.zardriver.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by Mehrdad on 1/7/2023
 */
@Singleton
class ResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    //---------------------------------------------------------------------------------------------- getString
    fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }
    //---------------------------------------------------------------------------------------------- getString

}