package com.victoria.bleled.util.feature;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

// Have to adjust resize on activity
public class KeyboardUtil {

    public final static int MAX_KEYBOARD_HEIGHT = 170;

    private KeyboardVisibleListener mListener = null;
    private View mRootView = null;
    private boolean mKeyboardOpend = false;
    private int mOldKeyboardHeight = 0;

    public KeyboardUtil(View rootView) {
        mRootView = rootView;
    }

    public void setKeyboardVisibleListner(KeyboardVisibleListener listner) {
        mListener = listner;

        if (mRootView == null) {
            return;
        }

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();

                mRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootView.getRootView().getHeight();

                int heightDiff = screenHeight - (r.bottom);
                int keyboardDiff = MAX_KEYBOARD_HEIGHT;

                if (heightDiff > keyboardDiff) { // 99% of the time the height diff will be due to a keyboard.
                    mKeyboardOpend = true;

                    if (mListener != null) {
                        mListener.onShow();
                    }
                } else if (mKeyboardOpend == true) {
                    mKeyboardOpend = false;

                    if (mListener != null) {
                        mListener.onHide();
                    }
                }

                if (mOldKeyboardHeight != heightDiff) {
                    if (mListener != null) {
                        mListener.onKeyboardHeight(heightDiff);
                    }
                }

                mOldKeyboardHeight = heightDiff;
            }
        });
    }

    public interface KeyboardVisibleListener {
        void onShow();

        void onHide();

        void onKeyboardHeight(int height);
    }
}
