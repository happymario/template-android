package com.victoria.bleled.app.special.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

class DayScheduleView : LinearLayout {
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
        var weekView = TextView(context)
        weekView.text = "일정이 없습니다."
        addView(weekView)
    }
}