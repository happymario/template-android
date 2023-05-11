package com.victoria.bleled.app.special.calendar

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.view.calendar.BaseCalendarView
import com.victoria.bleled.util.view.calendar.MonthCalendarAdapter
import java.util.Calendar
import java.util.Date

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        setupToolbar()
        setupCalendar()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun onCalendarLeft(view: View) {
        val calendar = findViewById<BaseCalendarView>(R.id.calendar)
        calendar.prevMonth()
    }

    fun onCalendarRight(view: View) {
        val calendar = findViewById<BaseCalendarView>(R.id.calendar)
        calendar.nextMonth()
    }


    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = resources.getStringArray(R.array.arr_special_tech)[1]
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCalendar() {

        val calendar = findViewById<BaseCalendarView>(R.id.calendar)

        calendar.setListener(object : MonthCalendarAdapter.Listener {
            override fun refreshCurrentMonth(calendar: Calendar) {
                showDate(calendar)
            }

            override fun selecteDate(date: Date) {
                // 현재 날짜보다 크면 무시
                val curDate = CommonUtil.getCurrentDate()
                val strDate = CommonUtil.getDateString("yyyyMMdd", date)
                if (strDate > curDate) {
                    return
                }
                // todo
            }
        })
    }

    private fun showDate(calendar: Calendar) {
        val tvDate = findViewById<TextView>(R.id.tv_date)
        tvDate.text = CommonUtil.getDateString(CommonUtil.TIME_FORMAT4, calendar.time)
    }
}