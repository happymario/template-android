package com.mario.template.ui.view.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BaseCalendar {
    public final static int DAYS_OF_WEEK = 7;
    public final static int ROWS_OF_CALENDAR = 6;

    public Calendar calendar = Calendar.getInstance();

    public int prevMonthTailOffset = 0;
    public int nextMonthHeadOffset = 0;
    public int currentMonthMaxDate = 0;

    public ArrayList<Integer> data = new ArrayList<>();

    public BaseCalendar() {
        calendar.setTime(new Date());
    }

    /**
     * Init calendar
     */
    public void initBaseCalendar(Listener refreshCallback) {
        makeMonthDate(refreshCallback);
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        return getDateString("yyyyMMdd", cal.getTime());
    }

    public static String getDateString(String format, Date date) {
        if (date == null)
            return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    /**
     * Change to prev month
     */
    public void changeToPrevMonth(Listener refreshCallback) {
        if (calendar.get(Calendar.MONTH) == 0) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        }
        makeMonthDate(refreshCallback);
    }

    /**
     * Change to next month
     */
    public void changeToNextMonth(Listener refreshCallback) {
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
            calendar.set(Calendar.MONTH, 0);
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        makeMonthDate(refreshCallback);
    }

    /**
     * make month date
     */
    public void makeMonthDate(Listener refreshCallback) {
        data.clear();
        calendar.set(Calendar.DATE, 1);

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        makePrevMonthTail((Calendar) calendar.clone());
        makeCurrentMonth(calendar);

        nextMonthHeadOffset =
                ROWS_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate);
        makeNextMonthHead();

        refreshCallback.onCalendar((Calendar) calendar.clone());
    }

    /**
     * Generate data for the last month displayed before the first day of the current calendar
     */
    private void makePrevMonthTail(Calendar calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        int maxDate = calendar.getActualMaximum(Calendar.DATE);
        int maxOffsetDate = maxDate - prevMonthTailOffset;

        for (int i = 1; i <= prevMonthTailOffset; i++) {
            data.add(++maxOffsetDate);
        }
    }

    /**
     * Generate data for the current calendar
     */
    private void makeCurrentMonth(Calendar calendar) {
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DATE); i++) {
            data.add(i);
        }
    }

    /**
     * Generate data for the next month displayed before the last day of the current calendar.
     */
    private void makeNextMonthHead() {
        int date = 1;
        for (int i = 1; i <= nextMonthHeadOffset; i++) {
            data.add(date++);
        }
    }

    interface Listener {
        void onCalendar(Calendar calendar);
    }
}
