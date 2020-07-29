package com.victoria.bleled.data.local;

import com.victoria.bleled.data.model.ModelAppInfo;
import com.victoria.bleled.data.model.ModelUser;

public interface IPrefDataSource {
    String getPushToken();

    void setPushToken(String token);

    double getLatitude();

    void setLatitude(double lat);

    double getLongitude();

    void setLongitude(double lng);

    long getNotSeeCoverTime();

    void setNotSeeCoverTime(long time);

    ModelUser getUser();  // 로그인 유저정보

    void setUser(ModelUser user);

    ModelAppInfo getAppInfo(); // 서버에서 받는 앱관련 정보

    void setAppInfo(ModelAppInfo user);

    boolean isAutoLogin();

    void setAutoLogin(boolean firstRun);

    String getRecommendID();

    void setRecommendID(String id);

    String getReferer();

    void setReferer(String referer);
}
