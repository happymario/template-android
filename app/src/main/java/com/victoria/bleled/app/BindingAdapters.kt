package com.victoria.bleled.app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.*
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.util.thirdparty.glide.ImageLoader
import com.victoria.bleled.util.view.recycleview.CustomDecoration

@BindingMethods(
    value = [
        BindingMethod(
            type = ImageView::class,
            attribute = "android:tint",
            method = "setImageTintList"
        )]
)
class BindingAdapters {
    companion object {
        @BindingAdapter("long_time")
        @JvmStatic
        fun setLongTime(view: TextView, newValue: Long) {
            // Important to break potential infinite loops.
            if (view.text.toString() != newValue.toString()) {
                view.text = newValue.toString()
            }
        }

        @InverseBindingAdapter(attribute = "long_time")
        @JvmStatic
        fun getLongTime(view: TextView): Long {
            return view.text.toString().toLongOrNull() ?: 0
        }
    }
}

@BindingConversion
fun convertColorToDrawable(color: Int) = ColorDrawable(color)

@BindingAdapter("android:selected")
fun setSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}

@BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
fun setImageUrl(
    imageView: ImageView,
    url: String?,
    placeHolder: Int? = R.drawable.xml_default_img
) {
    if (url == null) {
        imageView.setImageResource(placeHolder ?: R.drawable.xml_default_img)
    } else {
        ImageLoader.loadImage(
            imageView.context,
            imageView,
            placeHolder ?: R.drawable.xml_default_img,
            url
        )
    }
}

@BindingAdapter(value = ["dividerHeight", "dividerPadding", "dividerColor"], requireAll = false)
fun RecyclerView.setDivider(
    dividerHeight: Float?,
    dividerPadding: Float?,
    @ColorInt dividerColor: Int?
) {
    val decoration = CustomDecoration(
        height = dividerHeight ?: 0f,
        padding = dividerPadding ?: 0f,
        color = dividerColor ?: Color.TRANSPARENT
    )
    addItemDecoration(decoration)
}