package com.zarholding.zardriver.view.fragment.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumApiError
import com.zarholding.zardriver.R
import com.zarholding.zardriver.ZarFragment
import com.zarholding.zardriver.databinding.FragmentSplashBinding
import com.zarholding.zardriver.model.data.response.DriverModel
import com.zarholding.zardriver.tools.CompanionValues
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class SplashFragment(override var layout: Int = R.layout.fragment_splash):
    ZarFragment<FragmentSplashBinding>(){

    private val splashViewModel: SplashViewModel by viewModels()

    private var job: Job? = null


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserIsLogged()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let { (it as MainActivity).showMessage(message) }
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        if (splashViewModel.userIsEntered()) {
            observeLiveData()
            splashViewModel.requestGetDriverInfo()
        } else
            gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged



    //---------------------------------------------------------------------------------------------- observeLiveData
    private fun observeLiveData() {
        splashViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }

        splashViewModel.driverLiveData.observe(viewLifecycleOwner){
            gotoFragmentHome(it)
        }
    }
    //---------------------------------------------------------------------------------------------- observeLiveData



    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Main) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentLogin


    //---------------------------------------------------------------------------------------------- gotoFragmentHome
    private fun gotoFragmentHome(driverModel: DriverModel) {
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Main) {
                val bundle = Bundle()
                bundle.putParcelable(CompanionValues.driverModel, driverModel)
                findNavController().navigate(R.id.action_splashFragment_to_HomeFragment, bundle)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentHome


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}