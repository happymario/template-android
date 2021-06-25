package com.victoria.lib.arch.mvp;

/**
 * Created by codeest on 2016/8/2.
 * View基类
 */
public interface MBaseView {

    void useNightMode(boolean isNight);

    void stateError();

    void stateEmpty();

    void stateLoading();
}
