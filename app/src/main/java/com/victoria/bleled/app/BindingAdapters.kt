package com.victoria.bleled.app

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods

@BindingMethods(
    value = [BindingMethod(
        type = android.widget.ImageView::class,
        attribute = "android:tint",
        method = "setImageTintList"
    )]
)

object BindingAdapters {
    @BindingAdapter("app:isVisible")
    @JvmStatic
    fun setIsVisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE;
    }
}
