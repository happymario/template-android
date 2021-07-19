package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.victoria.bleled.R
import com.victoria.bleled.databinding.ActivityMainBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var pagerAdapter: MainPagerAdapter? = null

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }


    override fun onPause() {
        super.onPause()
    }


    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onMenu(view: View) {
        CommonUtil.showNIToast(this)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        setupToolbar()
        setupViewPager(binding.viewpager)
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = pagerAdapter?.getFragmentTitle(position)
        }.attach()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val ab: ActionBar? = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        pagerAdapter = MainPagerAdapter(this)

        val titleArray = ArrayList<String>()
        titleArray.add(getString(R.string.tab_main))
        titleArray.add(getString(R.string.tab_special))
        titleArray.add(getString(R.string.tab_latest))
        pagerAdapter?.setFragmentTitle(titleArray)

        viewPager.adapter = pagerAdapter
    }

    /************************************************************
     *  SubClasses
     ************************************************************/

}
