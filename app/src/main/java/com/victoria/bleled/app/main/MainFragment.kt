package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentMainBinding
import com.victoria.bleled.util.arch.base.BaseBindingFragment


class MainFragment : BaseBindingFragment<FragmentMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
//    private lateinit var viewModel: MainViewModel
//    private lateinit var bannerAdapter: BannerAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        refresh()
    }


    /************************************************************
     *  Events
     ************************************************************/


    /************************************************************
     *  Public
     ************************************************************/
    fun refresh() {
        // TODO
    }


    /************************************************************
     *  Private Functions
     ************************************************************/
    override fun initView() {
        super.initView()
        binding.view = this
    }

    private fun initViewModel() {

    }
}