package com.zarholding.zardriver.view.fragment.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.di.ResourcesProvider
import com.zarholding.zardriver.model.request.LoginRequestModel
import com.zarholding.zardriver.model.repository.LoginRepository
import com.zarholding.zardriver.utility.CompanionValues
import com.zarholding.zardriver.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var repository: LoginRepository,
    private val resourcesProvider: ResourcesProvider,
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
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val user = userName.value.persianNumberToEnglishNumber()
            val pass = password.value.persianNumberToEnglishNumber()
            userName.postValue(user)
            password.postValue(pass)
            val response = repository.requestLogin(LoginRequestModel(user, pass))
            if (response?.isSuccessful == true) {
                val loginResponse = response.body()
                loginResponse?.let {
                    if (!it.hasError)
                        saveUserNameAndPassword(it.data)
                    setMessage(it.message)
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.driverInfoIsEmpty))
                }
            } else
                setMessage(response)
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