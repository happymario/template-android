package com.victoria.bleled.app.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.app.test.animation.AnimationTestActivity
import com.victoria.bleled.app.test.bluetooth.BluetoothTestActivity

class TestActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_test)

        initView()
    }

    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onCamera(view: View) {
        val intent = Intent(this, CameraTestActivity::class.java)
        startActivity(intent)
    }

    fun onBluetooth(view: View) {
        val intent = Intent(this, BluetoothTestActivity::class.java)
        startActivity(intent)
    }

    fun onAnimation(view: View) {
        val intent = Intent(this, AnimationTestActivity::class.java)
        startActivity(intent)
    }

    fun onEtc(view: View) {
        val intent = Intent(this, EtcTestActivity::class.java)
        startActivity(intent)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private fun initView() {
        // ui

        // event

    }


    /************************************************************
     *  Networking
     ************************************************************/


    /************************************************************
     *  SubClasses
     ************************************************************/

}