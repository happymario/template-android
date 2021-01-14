package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R

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
    fun onFinish(view: View) {
        finish()
    }

    /************************************************************
     *  Helpers
     ************************************************************/


    /************************************************************
     *  SubClasses
     ************************************************************/

}
