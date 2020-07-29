package com.victoria.bleled.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefMgr {
    public static final String PREFS_NAME = "MT_Prefs";

    private static PrefMgr preferences = null;

    private SharedPreferences mPreferences = null;

    public PrefMgr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mPreferences = prefs;
    }

    private PrefMgr() {

    }

    public void init(SharedPreferences sharedPreferences) {
        mPreferences = sharedPreferences;
    }

    public void put(String key, Object value) {
        if (value == null) {
            return;
        }

        SharedPreferences.Editor editor = mPreferences.edit();
        if (value.getClass().equals(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().equals(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass().equals(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else if (value.getClass().equals(Float.class) || value.getClass().equals(Double.class)) {
            editor.putFloat(key, (Float) value);
        } else if (value.getClass().equals(Long.class)) {
            editor.putLong(key, (Long) value);
        }

        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mPreferences.getFloat(key, defaultValue);
    }

    public Long getLong(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return mPreferences.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
