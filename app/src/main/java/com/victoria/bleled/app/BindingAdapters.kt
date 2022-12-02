package com.victoria.bleled.app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.databinding.*
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.app.main.TaskAdapter
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
object BindingAdapters {
    @BindingAdapter("items")
    fun setItems(listView: RecyclerView, items: List<String>?) {
        items?.let {
            (listView.adapter as TaskAdapter).submitList(items)
        }
    }


    @BindingAdapter("android:selected")
    fun setSelected(view: View, selected: Boolean) {
        view.isSelected = selected
    }

    @BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
    fun setImageUrl(
        imageView: ImageView,
        url: String?,
        placeHolder: Int? = R.drawable.xml_default_img,
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
        @ColorInt dividerColor: Int?,
    ) {
        val decoration = CustomDecoration(
            height = dividerHeight ?: 0f,
            padding = dividerPadding ?: 0f,
            color = dividerColor ?: Color.TRANSPARENT
        )
        addItemDecoration(decoration)
    }

    @BindingAdapter("android:layout_height")
    fun setLayoutHeight(view: View, height: Float) {
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        view.layoutParams = layoutParams
    }
}

object Converter {
    @InverseMethod("stringToFloat")
    @JvmStatic
    fun floatToString(value: Float): String {
        return if (value == 0f) {
            ""
        } else value.toString()
    }

    @JvmStatic
    fun stringToFloat(value: String?): Float {
        return if (value.isNullOrEmpty()) {
            0f
        } else try {
            value.toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
    }
}

@BindingConversion
fun convertColorToDrawable(color: Int) = ColorDrawable(color)