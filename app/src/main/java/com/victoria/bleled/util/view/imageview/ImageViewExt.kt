package com.victoria.bleled.util.view.imageview

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.victoria.bleled.R
import java.io.File

interface ImageLoadListener {
    fun onSuccess()

    fun onError()
}

fun ImageView.loadImage(imageUrl: String) {
    Glide.with(this.context.applicationContext).load(imageUrl).into(this)
}

fun ImageView.loadImage(imageUrl: String, default: Drawable) {
    Glide.with(this.context.applicationContext).load(imageUrl).placeholder(default).into(this)
}

fun ImageView.loadBitmap(image: Bitmap) {
    Glide.with(this).load(image).into(this)
}

fun ImageView.loadImage(imageUrl: String, defaultId: Int) {
    Glide.with(this).load(imageUrl).placeholder(defaultId).into(this)
}

fun ImageView.loadImage(url: String, defaultResId: Int, callback: ImageLoadListener?) {
    try {
        val file = File(url)
        if (file.exists()) {
            Glide.with(this)
                .load(File(url))
                .placeholder(defaultResId)
                .into(this)

            callback?.onSuccess()
        } else if (url.isNotEmpty()) {
            Glide.with(this)
                .load(File(url))
                .placeholder(defaultResId)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?, model: Any,
                        target: Target<Drawable>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        if (defaultResId != 0) {
                            this@loadImage.setImageResource(defaultResId)
                        } else {
                            this@loadImage.setImageResource(R.color.image_bg)
                        }

                        callback?.onError()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        callback?.onSuccess()

                        return false
                    }
                })
                .placeholder(defaultResId)
                .into(this)
        } else {
            if (defaultResId != 0) {
                Glide.with(this)
                    .load(defaultResId)
                    .into(this)
            } else {
                Glide.with(this)
                    .load(R.color.image_bg)
                    .into(this)
            }
        }
    } catch (e: Exception) {
        callback?.onError()

        if (defaultResId != 0) {
            Glide.with(this)
                .load(defaultResId)
                .into(this)
        } else {
            Glide.with(this)
                .load(R.color.image_bg)
                .into(this)
        }
    }
}