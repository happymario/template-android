package com.mario.lib.base.util

import android.graphics.Rect
import android.view.View

// Have to adjust resize on activity
class KeyboardUtil(rootView: View?) {
    private var mListener: KeyboardVisibleListener? = null
    private var mRootView: View? = null
    private var mKeyboardOpend = false
    private var mOldKeyboardHeight = 0

    init {
        mRootView = rootView
    }

    fun setKeyboardVisibleListner(listner: KeyboardVisibleListener?) {
        mListener = listner
        if (mRootView == null) {
            return
        }
        mRootView!!.getViewTreeObserver().addOnGlobalLayoutListener {
            val r = Rect()
            mRootView!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = mRootView!!.getRootView().height
            val heightDiff = screenHeight - r.bottom
            val keyboardDiff = MAX_KEYBOARD_HEIGHT
            if (heightDiff > keyboardDiff) { // 99% of the time the height diff will be due to a keyboard.
                mKeyboardOpend = true
                if (mListener != null) {
                    mListener!!.onShow()
                }
            } else if (mKeyboardOpend == true) {
                mKeyboardOpend = false
                if (mListener != null) {
                    mListener!!.onHide()
                }
            }
            if (mOldKeyboardHeight != heightDiff) {
                if (mListener != null) {
                    mListener!!.onKeyboardHeight(heightDiff)
                }
            }
            mOldKeyboardHeight = heightDiff
        }
    }

    interface KeyboardVisibleListener {
        fun onShow()
        fun onHide()
        fun onKeyboardHeight(height: Int)
    }

    companion object {
        const val MAX_KEYBOARD_HEIGHT = 170
    }
}
