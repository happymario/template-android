package com.victoria.bleled.app.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.databinding.ActivityLoginBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity

class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }


    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onLogin(view: View) {
        var email = binding.etId.text.toString()
        var pwd = binding.etPwd.text.toString()

        if (!CommonUtil.isValidEmail(email)) {
            CommonUtil.showToast(this, R.string.input_valid_email)
            return
        }

        if (pwd.length < 6) {
            CommonUtil.showToast(this, R.string.input_valid_pwd)
            return
        }

        reqLogin(email, pwd)
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
    }

    private fun hideKeyboard() {
        CommonUtil.hideKeyboard(binding.etId)
        CommonUtil.hideKeyboard(binding.etPwd)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.INTENT_TYPE, true)
        }
        startActivity(intent)
    }

    private fun goSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    /************************************************************
     *  Networking
     ************************************************************/
    private fun reqLogin(email: String, pwd: String) {
        // TODO Login
        goMain()
    }

    /************************************************************
     *  SubClasses
     ************************************************************/
}

