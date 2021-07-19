package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.View
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


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()

        refresh()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onResume() {
        super.onResume()
    }


    /************************************************************
     *  Events
     ************************************************************/
    fun onClickTab(view: View) {

    }

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