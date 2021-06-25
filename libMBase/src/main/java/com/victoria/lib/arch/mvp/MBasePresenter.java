package com.victoria.lib.arch.mvp;

/**
 * Created by codeest on 2016/8/2.
 * Presenter基类
 */
public interface MBasePresenter<T extends MBaseView> {

    void attachView(T view);

    void detachView();
}
