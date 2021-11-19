package com.victoria.bleled.app.user

import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import androidx.activity.viewModels
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.data.remote.ApiException
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.databinding.ActivityLoginBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory

class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<UserViewModel> { getViewModelFactory() }

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        initViewModel()
    }

    /************************************************************
     *  Private
     ************************************************************/
    override fun initView() {
        super.initView()

        // init values
        binding.btnMain.visibility = View.GONE
        if (Constants.IS_TEST) {
            binding.btnMain.visibility = View.VISIBLE
        }
        binding.btnAsk.paintFlags = binding.btnAsk.paintFlags or TextPaint.UNDERLINE_TEXT_FLAG

        // init events
        binding.llContent.setOnClickListener {
            hideKeyboard()
        }
        binding.btnSignup.setOnClickListener {
            goSignup()
        }
        binding.btnMain.setOnClickListener {
            goMain()
        }
        binding.btnAsk.setOnClickListener {
            CommonUtil.gotoPhone(this, Constants.CLIENT_PHONE_NUMBER)
        }
    }

    private fun initViewModel() {
        binding.viewmodel = viewModel

        viewModel.toastMessage.observe(this, { msg ->
            CommonUtil.showToast(this, msg)
        })

        viewModel.dataLoading.observe(this, { loading ->
            if (loading) {
                showProgress()
            } else {
                hideProgress()
            }
        })

        viewModel.networkErrorLiveData.observe(this, { error ->
            val exception = error.error
            if (exception is ApiException && exception.code == ApiException.ERR_NO_USER) {
                CommonUtil.showToast(this, R.string.msg_no_login_user)
            } else {
                val msg = NetworkObserver.getErrorMsg(this, error)
                CommonUtil.showToast(
                    this,
                    if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
                )
            }
        })

        viewModel.loginCompleteEvent.observe(this, {
            goMain()
        })
    }

    private fun hideKeyboard() {
        CommonUtil.hideKeyboard(binding.etId)
        CommonUtil.hideKeyboard(binding.etPwd)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.ARG_TYPE, true)
        }
        startActivity(intent)
    }

    private fun goSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}

