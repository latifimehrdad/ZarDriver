package com.zarholding.zardriver.view.fragment.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarFragment
import com.zarholding.zardriver.databinding.FragmentLoginBinding
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment(override var layout: Int = R.layout.fragment_login)
    :ZarFragment<FragmentLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = loginViewModel
        initView()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        activity?.let { (it as MainActivity).deleteAllData() }
        setClickOnViews()
        observeLiveDate()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let { (it as MainActivity).showMessage(message) }
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- observeLiveDate
    private fun observeLiveDate() {
        loginViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            binding.buttonLogin.stopLoading()
            showMessage(it.message)
        }

        loginViewModel.loginLiveDate.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        loginViewModel.userNameError.observe(viewLifecycleOwner) {
            binding.textInputLayoutUserName.error = it
            stopLoading()
        }

        loginViewModel.passwordError.observe(viewLifecycleOwner) {
            binding.textInputLayoutPasscode.error = it
            stopLoading()
        }

    }
    //---------------------------------------------------------------------------------------------- observeLiveDate



    //---------------------------------------------------------------------------------------------- setClickOnViews
    private fun setClickOnViews() {

        binding.editTextUserName.setOnClickListener {
            binding.textInputLayoutUserName.error = null
        }

        binding.editTextPassword.setOnClickListener {
            binding.textInputLayoutPasscode.error = null
        }

        binding.buttonLogin.setOnClickListener { login() }

    }
    //---------------------------------------------------------------------------------------------- setClickOnViews


    //---------------------------------------------------------------------------------------------- login
    private fun login() {
        if (binding.buttonLogin.isLoading)
            return
        startLoading()
        loginViewModel.login()
    }
    //---------------------------------------------------------------------------------------------- login



    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        binding.textInputLayoutUserName.error = null
        binding.textInputLayoutPasscode.error = null
        binding.editTextUserName.isEnabled = false
        binding.editTextPassword.isEnabled = false
        binding.buttonLogin.startLoading(getString(R.string.bePatient))
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.editTextUserName.isEnabled = true
        binding.editTextPassword.isEnabled = true
        binding.buttonLogin.stopLoading()
    }
    //---------------------------------------------------------------------------------------------- stopLoading


}