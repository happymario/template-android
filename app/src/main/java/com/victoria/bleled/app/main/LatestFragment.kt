package com.victoria.bleled.app.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.victoria.bleled.R
import com.victoria.bleled.app.test.CameraTestActivity
import com.victoria.bleled.app.test.EtcTestActivity
import com.victoria.bleled.app.test.animation.AnimationTestActivity
import com.victoria.bleled.databinding.FragmentLatestBinding
import com.victoria.bleled.util.arch.base.BaseBindingFragment


class LatestFragment : BaseBindingFragment<FragmentLatestBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance() =
            LatestFragment().apply {
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
        return R.layout.fragment_latest
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        refresh()
    }

    override fun onResume() {
        super.onResume()
    }


    /************************************************************
     *  Events
     ************************************************************/
    fun onCamera(view: View) {
        val intent = Intent(activity, CameraTestActivity::class.java)
        startActivity(intent)
    }

    fun onAnimation(view: View) {
        val intent = Intent(activity, AnimationTestActivity::class.java)
        startActivity(intent)
    }

    fun onEtc(view: View) {
        val intent = Intent(activity, EtcTestActivity::class.java)
        startActivity(intent)
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
    fun initView() {
        binding.view = this
    }

    private fun initViewModel() {

    }
}