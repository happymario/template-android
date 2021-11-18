package com.victoria.bleled.app.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
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
     *  Event Handler
     ************************************************************/
    fun onLogin(view: View) {
        val email = binding.etId.text.toString()
        val pwd = binding.etPwd.text.toString()

        if (!CommonUtil.isValidEmail(email)) {
            CommonUtil.showToast(this, R.string.input_valid_email)
            return
        }

        if (pwd.length < 6) {
            CommonUtil.showToast(this, R.string.input_valid_pwd)
            return
        }

        viewModel.loginUser(email, pwd)
    }

    fun onSignup(view: View) {
        goSignup()
    }

    fun onMain(view: View) {
        goMain()
    }

    fun onAsk(view: View) {
        CommonUtil.gotoPhone(this, Constants.CLIENT_PHONE_NUMBER)
    }


    /************************************************************
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        binding.llContent.setOnClickListener {
            hideKeyboard()
        }

        binding.btnMain.visibility = View.GONE
        if(Constants.IS_TEST) {
            binding.btnMain.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        viewModel.dataLoading.observe(this, Observer { loading ->
            if (loading) {
                showProgress()
            } else {
                hideProgress()
            }
        })

        viewModel.networkErrorLiveData.observe(this, Observer { error ->
            val exception = error.error
            if(exception is ApiException && exception.code == ApiException.ERR_NO_USER) {
                CommonUtil.showToast(this, R.string.msg_no_login_user)
            }
            else {
                val msg = NetworkObserver.getErrorMsg(this, error)
                CommonUtil.showToast(
                    this,
                    if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
                )
            }
        })

        viewModel.loginCompleteEvent.observe(this, Observer {
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

