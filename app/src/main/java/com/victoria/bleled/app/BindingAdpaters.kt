package com.victoria.bleled.app

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.*
import com.victoria.bleled.R
import com.victoria.bleled.util.thirdparty.glide.ImageLoader

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
        @BindingAdapter("time")
        @JvmStatic
        fun setTime(view: TextView, newValue: Long) {
            // Important to break potential infinite loops.
            if (view.text.toString() != newValue.toString()) {
                view.text = newValue.toString()
            }
        }

        @InverseBindingAdapter(attribute = "time")
        @JvmStatic
        fun getTime(view: TextView): Long {
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
fun setImageUrl(imageView: ImageView, url: String?, placeHolder: Int?) {
    if (url == null) {
        imageView.setImageResource(placeHolder ?: 0)
    } else {
        ImageLoader.loadImage(
            imageView.context,
            imageView,
            placeHolder ?: R.drawable.xml_bg_default_img,
            url
        )
    }
}