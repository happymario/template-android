package com.mario.lib.base.extension

import android.view.View

object ViewExt {

    fun View.show(shouldBeShow: Boolean) {
        visibility = if (shouldBeShow) View.VISIBLE else View.GONE
    }
}