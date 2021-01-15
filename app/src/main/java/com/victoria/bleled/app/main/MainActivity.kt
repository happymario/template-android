package com.victoria.bleled.app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.app.main.mode.MonthCalendarFragment
import com.victoria.bleled.app.test.TestActivity
import com.victoria.bleled.util.CommonUtil

class MainActivity : AppCompatActivity() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var fragmentMonthCalendar : MonthCalendarFragment = MonthCalendarFragment()

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
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

    fun onMenu(view: View) {
        CommonUtil.showNIToast(this)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private fun initView() {
        // view
        initFragment()

        // event

    }

    private fun initFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_main_content, fragmentMonthCalendar)
        fragmentTransaction.commit()
    }


    /************************************************************
     *  SubClasses
     ************************************************************/

}
