package com.mario.template.ui.layout.view.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mario.template.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class BaseCalendarView extends FrameLayout {
    public final static int WEEK_VIEW_H = 50;

    private LinearLayout weeksLayout;
    private RecyclerView daysRecyclerView;
    private MonthCalendarAdapter adapter;
    private MonthCalendarAdapter.Listener listener = null;

    public BaseCalendarView(Context context) {
        super(context);
    }

    public BaseCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public BaseCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(
                context,
                attrs,
                defStyleAttr
        );
        init(attrs, defStyleAttr, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        initWeekView();
        initDayView();
    }

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float px = dp * (metrics.densityDpi / 160f);

        return (int) px;
    }

    private void initWeekView() {
        weeksLayout = new LinearLayout(getContext());

        String[] arrWeekName = getResources().getStringArray(R.array.week);
        for (int i = 0; i < BaseCalendar.DAYS_OF_WEEK; i++) {
            TextView weekView = new TextView(getContext());
            weekView.setText(arrWeekName[i]);

            if (i == 0) {
                weekView.setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                weekView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            weekView.setTextSize(16.0f);
            weekView.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1.0f;
            weeksLayout.addView(weekView, layoutParams);
        }

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(getContext(), WEEK_VIEW_H));
        addView(weeksLayout, layoutParams);
    }


    private void initDayView() {
        daysRecyclerView = new RecyclerView(getContext());
        daysRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), BaseCalendar.DAYS_OF_WEEK));

        adapter = new MonthCalendarAdapter(getContext(), new MonthCalendarAdapter.Listener() {

            @Override
            public void refreshCurrentMonth(@NotNull Calendar calendar) {
                listener.refreshCurrentMonth(calendar);
            }

            @Override
            public void selecteDate(@NotNull Date date) {
                listener.selecteDate(date);
            }
        });
        daysRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = dpToPx(getContext(), WEEK_VIEW_H);
        addView(daysRecyclerView, layoutParams);
    }

    public void setListener(MonthCalendarAdapter.Listener listener) {
        this.listener = listener;
    }

    public void nextMonth() {
        adapter.changeToNextMonth();
    }

    public void prevMonth() {
        adapter.changeToPrevMonth();
    }
}
