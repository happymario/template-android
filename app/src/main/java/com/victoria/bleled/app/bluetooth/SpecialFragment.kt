package com.victoria.bleled.app.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentSpecialBinding
import com.victoria.bleled.util.arch.base.BaseBindingFragment


class SpecialFragment : BaseBindingFragment<FragmentSpecialBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance() =
            SpecialFragment().apply {
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
    override fun getLayoutId(): Int {
        return R.layout.fragment_special
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initView()
        return view
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