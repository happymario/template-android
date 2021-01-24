package com.victoria.bleled.app.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.databinding.ActivitySignupBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.base.BaseBindingActivity

class SignupActivity : BaseBindingActivity<ActivitySignupBinding>() {
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
        setContentView(R.layout.activity_signup)

        initView()
    }


    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onBack(view: View) {
        finish()
    }

    fun onSignup(view: View) {
        var email = binding.etId.text.toString()
        var pwd = binding.etPwd.text.toString()
        var pwdConfirm = binding.etPwdConfrim.text.toString()
        var motto = binding.etMotto.text.toString()

        if (!CommonUtil.isValidEmail(email)) {
            CommonUtil.showToast(this, R.string.input_valid_email)
            return
        }

        if (pwd.length < 6) {
            CommonUtil.showToast(this, R.string.input_valid_pwd)
            return
        }

        if (pwd != pwdConfirm) {
            CommonUtil.showToast(this, R.string.hint_pwd_confirm)
            return
        }

        reqSignup(email, pwd, motto)
    }


    /************************************************************
     *  Helpers
     ************************************************************/
    private fun initView() {
        // ui


        // event
        binding.llContent.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        CommonUtil.hideKeyboard(binding.etId)
        CommonUtil.hideKeyboard(binding.etPwd)
        CommonUtil.hideKeyboard(binding.etPwdConfrim)
        CommonUtil.hideKeyboard(binding.etMotto)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.INTENT_TYPE, true)
        }
        startActivity(intent)
    }

    /************************************************************
     *  Networking
     ************************************************************/
    private fun reqSignup(email: String, pwd: String, motto: String) {
        // TODO Login
        goMain()
    }

    /************************************************************
     *  SubClasses
     ************************************************************/

}
