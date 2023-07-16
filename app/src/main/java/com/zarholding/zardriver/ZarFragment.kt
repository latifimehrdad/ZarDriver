package com.zarholding.zardriver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zarholding.zardriver.view.activity.MainActivity

/**
 * Created by m-latifi on 10/8/2022.
 */

abstract class ZarFragment<DB : ViewDataBinding> : Fragment() {

    abstract var layout : Int
    protected lateinit var binding : DB


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView



    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- gotoFragment
    protected fun gotoFragment(fragment: Int, bundle: Bundle? = null) {
        try {
            findNavController().navigate(fragment, bundle)
        } catch (e: java.lang.Exception){
            findNavController().navigate(R.id.action_goto_SplashFragment)
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragment


}