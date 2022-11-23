package com.zarholding.zardriver.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.R
import com.zarholding.zardriver.databinding.FragmentSplashBinding
import com.zarholding.zardriver.model.response.driver.DriverModel
import com.zarholding.zardriver.utility.CompanionValues
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.viewmodel.DriverViewModel
import com.zarholding.zardriver.viewmodel.TokenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class SplashFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val tokenViewModel: TokenViewModel by viewModels()
    private val driverViewModel: DriverViewModel by viewModels()

    private var job: Job? = null
    lateinit var driverModel: DriverModel


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        checkUserIsLogged()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {

    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        val token = sharedPreferences.getString(CompanionValues.spToken, null)
        token?.let {
            requestGetDriverInfo()
        } ?: gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    private fun requestGetDriverInfo() {
        driverViewModel.requestGetDriverInfo(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let {
                    it.data?.let { info ->
                        driverModel = info
                        gotoFragmentHome()
                    } ?: run {
                        onError(
                            EnumErrorType.UNKNOWN,
                            requireContext().getString(R.string.driverInfoIsEmpty)
                        )
                    }
                } ?: run {
                    onError(
                        EnumErrorType.UNKNOWN,
                        requireContext().getString(R.string.driverInfoIsEmpty)
                    )
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo


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
    private fun gotoFragmentHome() {
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
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}