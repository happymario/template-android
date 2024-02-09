package com.mario.lib.base.extensions

import android.view.View

object ViewExtensions {

    fun View.show(shouldBeShow: Boolean) {
        visibility = if (shouldBeShow) View.VISIBLE else View.GONE
    }
}