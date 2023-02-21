package com.zarholding.zardriver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

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


}