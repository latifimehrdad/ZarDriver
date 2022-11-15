package com.zarholding.zardriver.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.R
import com.zarholding.zardriver.databinding.FragmentLoginBinding
import com.zarholding.zardriver.model.request.LoginRequestModel
import com.zarholding.zardriver.utility.CompanionValues
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val loginViewModel: LoginViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = loginViewModel
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setClickOnViews()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Log.d("meri", message)
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- setClickOnViews
    private fun setClickOnViews() {

        binding.editTextUserName.setOnClickListener {
            binding.textInputLayoutUserName.error = null
        }

        binding.editTextPassword.setOnClickListener {
            binding.textInputLayoutPasscode.error = null
        }

        binding.buttonLogin.setOnClickListener { checkEmptyValueForLogin() }

    }
    //---------------------------------------------------------------------------------------------- setClickOnViews


    //---------------------------------------------------------------------------------------------- checkEmptyValueForLogin
    private fun checkEmptyValueForLogin() {

        if (loginViewModel.loadingLiveDate.value == true)
            return

        if (loginViewModel.userName.isNullOrEmpty()) {
            binding.textInputLayoutUserName.error = getString(R.string.userNameValidationFailed)
            return
        }
        if (loginViewModel.passcode.isNullOrEmpty()) {
            binding.textInputLayoutPasscode.error = getString(R.string.passwordValidationFailed)
            return
        }
        requestLogin()
    }
    //---------------------------------------------------------------------------------------------- checkEmptyValueForLogin


    //---------------------------------------------------------------------------------------------- requestLogin
    private fun requestLogin() {

        val model = LoginRequestModel(
            loginViewModel.userName!!,
            loginViewModel.passcode!!
        )
        startLoading()
        loginViewModel.requestLogin(model).observe(viewLifecycleOwner) { response ->
            stopLoading()
            response?.let {
                if (it.hasError) {
                    onError(EnumErrorType.UNKNOWN, it.message)
                } else {
                    sharedPreferences
                        .edit()
                        .putString(CompanionValues.sharedPreferencesToken, it.data)
                        .apply()
                    stopLoading()
                    if (activity != null)
                        requireActivity().onBackPressed()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        binding.textInputLayoutUserName.error = null
        binding.textInputLayoutPasscode.error = null
        binding.editTextUserName.isEnabled = false
        binding.editTextPassword.isEnabled = false
        loginViewModel.loadingLiveDate.value = true
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.editTextUserName.isEnabled = true
        binding.editTextPassword.isEnabled = true
        loginViewModel.loadingLiveDate.value = false
    }
    //---------------------------------------------------------------------------------------------- stopLoading


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}