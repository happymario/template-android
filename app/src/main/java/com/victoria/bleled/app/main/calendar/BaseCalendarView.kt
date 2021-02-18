package com.victoria.bleled.app.main.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.util.CommonUtil
import java.util.*

class BaseCalendarView : FrameLayout {

    companion object {
        const val WEEK_VIEW_H = 30
    }

    private lateinit var weeksLayout: LinearLayout
    private lateinit var daysRecyclerView: RecyclerView
    private lateinit var adapter: MonthCalendarAdapter


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr, defStyleAttr)
    }

    private fun init(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        initWeekView()
        initDayView()
    }

    private fun initWeekView() {
        weeksLayout = LinearLayout(context)

        val arrWeekName = resources.getStringArray(R.array.week)
        for (i in 0 until BaseCalendar.DAYS_OF_WEEK) {
            var weekView = TextView(context)
            weekView.text = arrWeekName[i]

            if (i == 0) {
                weekView.setTextColor(resources.getColor(R.color.colorAccent))
            } else {
                weekView.setTextColor(resources.getColor(R.color.color_text))
            }
            weekView.textSize = 20f

            val layoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            layoutParams.weight = 1f
            weeksLayout.addView(weekView, layoutParams)
        }

        val layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, CommonUtil.dpToPx(context, WEEK_VIEW_H))
        addView(weeksLayout, layoutParams)
    }

    private fun initDayView() {
        daysRecyclerView = RecyclerView(context)
        daysRecyclerView.layoutManager =
            GridLayoutManager(context, BaseCalendar.DAYS_OF_WEEK)

        adapter = MonthCalendarAdapter(context, object : MonthCalendarAdapterListener {
            override fun refreshCurrentMonth(calendar: Calendar) {
                //val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
                //viewDataBinding.tvMonth.text = sdf.format(calendar.time)
            }
        })
        daysRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.topMargin = CommonUtil.dpToPx(context!!, WEEK_VIEW_H)
        addView(daysRecyclerView, layoutParams)
    }
}