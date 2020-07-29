package com.kyad.libmrbase.arch.mvp;

/**
 * Created by codeest on 2016/8/2.
 * Presenter基类
 */
public interface MrBasePresenter<T extends MrBaseView> {

    void attachView(T view);

    void detachView();
}
