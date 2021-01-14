package com.victoria.bleled.app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.app.test.TestActivity

class MainActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_main)

    }


    override fun onPause() {
        super.onPause()
    }


    /************************************************************
     *  Event Handler
     ************************************************************/
    fun onTest(view: View) {
        val intent = Intent(this, TestActivity::class.java)
        startActivity(intent)
    }

    /************************************************************
     *  Helpers
     ************************************************************/


    /************************************************************
     *  SubClasses
     ************************************************************/

}
