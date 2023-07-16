package com.zarholding.zardriver.view.fragment.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.model.data.request.LoginRequestModel
import com.zarholding.zardriver.model.repository.LoginRepository
import com.zarholding.zardriver.tools.CompanionValues
import com.zarholding.zardriver.tools.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var repository: LoginRepository,
    private val sharedPreferences: SharedPreferences
) : ZarViewModel() {

    val loginLiveDate : SingleLiveEvent<String?> by lazy { SingleLiveEvent<String?>() }
    val userNameError : SingleLiveEvent<String> by lazy { SingleLiveEvent<String>() }
    val passwordError : SingleLiveEvent<String> by lazy { SingleLiveEvent<String>() }
    val userName : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val password : MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //---------------------------------------------------------------------------------------------- login
    fun login() {
        var valueIsEmpty = false
        if (userName.value.isNullOrEmpty()) {
            userNameError.postValue(resourcesProvider.getString(R.string.userNameValidationFailed))
            valueIsEmpty = true
        }
        if (password.value.isNullOrEmpty()) {
            passwordError.postValue(resourcesProvider.getString(R.string.passwordValidationFailed))
            valueIsEmpty = true
        }
        if (!valueIsEmpty)
            requestLogin()
    }
    //---------------------------------------------------------------------------------------------- login


    //---------------------------------------------------------------------------------------------- requestLogin
    private fun requestLogin() {
        viewModelScope.launch(IO + exceptionHandler()) {
            val user = userName.value.persianNumberToEnglishNumber()
            val pass = password.value.persianNumberToEnglishNumber()
            val response = callApi(repository.requestLogin(LoginRequestModel(user, pass)))
            response?.let { saveUserNameAndPassword(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword
    private fun saveUserNameAndPassword(token: String?) {
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, token)
            .putString(CompanionValues.userName, userName.value)
            .putString(CompanionValues.passcode, password.value)
            .apply()
        loginLiveDate.postValue(token)
    }
    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword


}