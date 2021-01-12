package com.victoria.bleled.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.service.IMMResult
import com.victoria.bleled.util.arch.base.BaseActivity
import com.victoria.bleled.util.feature.KeyboardUtil
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    val result = IMMResult()

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startLogin()
    }

    /************************************************************
     *  Event Handler
     ************************************************************/


    /************************************************************
     *  Helpers
     ************************************************************/
    private fun startLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            goMain()
        }, 2000)
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


    /************************************************************
     *  SubClasses
     ************************************************************/
}