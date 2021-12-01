package com.victoria.bleled.app.special.bluetooth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import com.victoria.bleled.R
import com.victoria.bleled.databinding.ActivityTestBluetoothBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

class BluetoothTestActivity : BaseBindingActivity<ActivityTestBluetoothBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {

    }


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<BluetoothViewModel> { getViewModelFactory() }

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bluetooth)

        initView()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bluetooth, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        if (item.itemId === R.id.menu_scan) {
            CommonUtil.showNIToast(this)
        }
        if (item.itemId === R.id.menu_setting) {
            CommonUtil.showNIToast(this)
        }
        return super.onOptionsItemSelected(item)
    }


    /************************************************************
     *  Privates
     ************************************************************/
    override fun initView() {
        super.initView()
        setupToolbar()
    }

    private fun initViewModel() {
        binding.viewmodel = viewModel
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = resources.getStringArray(R.array.arr_special_tech)[0]
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}
