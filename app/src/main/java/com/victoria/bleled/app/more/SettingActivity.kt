package com.victoria.bleled.app.more

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import com.victoria.bleled.R
import com.victoria.bleled.app.etc.WebViewActivity
import com.victoria.bleled.app.user.LoginActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.common.dialog.AlertDialog
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseActivity
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

class SettingActivity : BaseActivity() {
    /************************************************************
     *  Variables
     ************************************************************/
    private val viewModel by viewModels<SettingViewModel> { getViewModelFactory() }


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setupToolbar()
        initViewModel()

        findViewById<TextView>(R.id.tv_ver).text = CommonUtil.getVersionName(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /************************************************************
     *  Events
     ************************************************************/
    fun onAlarm(view: View) {
        view.isSelected = !view.isSelected
    }

    fun onAgreement(view: View) {
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtra(Constants.ARG_TYPE, getString(R.string.use_agreement))
            putExtra(Constants.ARG_DATA, Constants.URL_USE_AGREE)
        }

        startActivity(intent)
    }

    fun onSignout(view: View) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.msg_signout))
            .setPositiveButton(R.string.confirm) { dialog, which ->
                viewModel.signOut()
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
            }
            .show()
    }


    /************************************************************
     *  Privates
     ************************************************************/
    private fun initViewModel() {
        viewModel.dataLoading.observe(this, {
            if (it == true) {
                showProgress()
            } else {
                hideProgress()
            }
        })

        viewModel.networkErrorLiveData.observe(this, { error ->
            val msg = NetworkObserver.getErrorMsg(this, error)
            CommonUtil.showToast(
                this,
                if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
            )
        })

        viewModel.userInfo.observe(this, {
            if (it == null) {
                goLogin()
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = ""
        ab?.setDisplayHomeAsUpEnabled(true)
    }


    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}