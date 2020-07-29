package com.victoria.bleled.data.local;

import android.content.Context;

import com.google.gson.Gson;
import com.victoria.bleled.data.model.ModelAppInfo;
import com.victoria.bleled.data.model.ModelUser;

public class PrefDataSourceImpl implements IPrefDataSource {
    private static final String PREFS_USER_INFO = "PREFS_USER_INFO";
    private static final String PREFS_APP_INFO = "PREFS_APP_INFO";
    private static final String PREFS_TOKEN = "PREFS_TOKEN";
    private static final String PREFS_GEO_LATITUDE = "PREFS_GEO_LATITUDE";
    private static final String PREFS_GEO_LOGITUDE = "PREFS_GEO_LOGITUDE";
    private static final String PREFS_COVER_NOT_SEE_TIME = "PREFS_COVER_NOT_SEE_TIME";
    private static final String PREFS_REFERER = "PREFS_REFERER";
    private static final String PREFS_RECOMMEND_ID = "PREFS_RECOMMEND_ID";
    private static final String PREFS_AUTO_LOGIN = "PREFS_AUTO_LOGIN";
    private static final String PREFS_APP_SETTING = "PREFS_APP_SETTING";

    private Context mContext;
    private PrefMgr mPreference;

    public static PrefDataSourceImpl getInstance(Context context) {
        PrefDataSourceImpl prefDataSource = new PrefDataSourceImpl(context);
        return prefDataSource;
    }

    PrefDataSourceImpl(Context context) {
        mContext = context;
        mPreference = new PrefMgr(context);
    }


    @Override
    public ModelUser getUser() {
        ModelUser modelUser = new Gson().fromJson(mPreference.getString(PREFS_USER_INFO, ""), ModelUser.class);
        return modelUser;
    }

    @Override
    public void setUser(ModelUser user) {
        if (user == null) {
            mPreference.put(PREFS_USER_INFO, "");
            return;
        }
        mPreference.put(PREFS_USER_INFO, new Gson().toJson(user));
    }

    @Override
    public String getPushToken() {
        return mPreference.getString(PREFS_TOKEN, "");
    }

    @Override
    public void setPushToken(String token) {
        mPreference.put(PREFS_TOKEN, token);
    }

    @Override
    public double getLatitude() {
        String strLatitude = mPreference.getString(PREFS_GEO_LATITUDE, "0");
        return Double.parseDouble(strLatitude);
    }

    @Override
    public void setLatitude(double lat) {
        mPreference.put(PREFS_GEO_LATITUDE, String.format("%f", lat));
    }

    @Override
    public double getLongitude() {
        String strLongititude = mPreference.getString(PREFS_GEO_LOGITUDE, "0");
        return Double.parseDouble(strLongititude);
    }

    @Override
    public void setLongitude(double lng) {
        mPreference.put(PREFS_GEO_LOGITUDE, String.format("%f", lng));
    }

    @Override
    public void setNotSeeCoverTime(long time) {
        mPreference.put(PREFS_COVER_NOT_SEE_TIME, time);
    }

    @Override
    public long getNotSeeCoverTime() {
        return mPreference.getLong(PREFS_COVER_NOT_SEE_TIME, 0);
    }

    @Override
    public ModelAppInfo getAppInfo() {
        ModelAppInfo modelUser = new Gson().fromJson(mPreference.getString(PREFS_APP_INFO, ""), ModelAppInfo.class);
        return modelUser;
    }

    @Override
    public void setAppInfo(ModelAppInfo user) {
        if (user == null) {
            mPreference.put(PREFS_APP_INFO, "");
            return;
        }
        mPreference.put(PREFS_APP_INFO, new Gson().toJson(user));
    }

    @Override
    public boolean isAutoLogin() {
        return mPreference.getBoolean(PREFS_AUTO_LOGIN, false);
    }

    @Override
    public void setAutoLogin(boolean fristRun) {
        mPreference.put(PREFS_AUTO_LOGIN, fristRun);
    }

    @Override
    public String getRecommendID() {
        return mPreference.getString(PREFS_RECOMMEND_ID, "");
    }

    @Override
    public void setRecommendID(String id) {
        mPreference.put(PREFS_RECOMMEND_ID, id);
    }

    @Override
    public String getReferer() {
        return mPreference.getString(PREFS_REFERER, "");
    }

    @Override
    public void setReferer(String referer) {
        mPreference.put(PREFS_REFERER, referer);
    }
}
