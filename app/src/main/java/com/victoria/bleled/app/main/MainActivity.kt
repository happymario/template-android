package com.victoria.bleled.app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.viewpager.widget.ViewPager
import com.victoria.bleled.R
import com.victoria.bleled.app.bluetooth.SpecialFragment
import com.victoria.bleled.app.test.LatestFragment
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
        binding.tabs.setupWithViewPager(binding.viewpager)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val ab: ActionBar? = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        pagerAdapter?.addFragment(MainFragment.newInstance(), getString(R.string.tab_main))
        pagerAdapter?.addFragment(SpecialFragment.newInstance(), getString(R.string.tab_special))
        pagerAdapter?.addFragment(LatestFragment.newInstance(), getString(R.string.tab_latest))

        viewPager.adapter = pagerAdapter
    }

    /************************************************************
     *  SubClasses
     ************************************************************/

}
