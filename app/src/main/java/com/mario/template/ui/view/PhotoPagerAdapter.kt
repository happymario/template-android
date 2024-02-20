package com.mario.template.ui.view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mario.lib.base.util.ValidUtil.isValidUrl
import com.mario.lib.base.view.imageview.TouchImageView
import com.mario.template.R
import com.mario.template.helper.thirdparty.glide.GlideImageLoader

class PhotoPagerAdapter internal constructor(var context: Context, modelPhotos: List<String>) :
    PagerAdapter() {
    var photos: List<String> = ArrayList()
    private var listener: AdapterListener? = null

    init {
        photos = modelPhotos
    }

    fun setListener(adapterListener: AdapterListener?) {
        listener = adapterListener
    }

    override fun getCount(): Int {
        return photos.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_pager_photo, null)
        val ivSliderImage = itemView.findViewById<View>(R.id.iv_photo) as TouchImageView
        ivSliderImage.setZoom(0.99f)
        ivSliderImage.resetZoom()
        val photo = photos[position]
        if (isValidUrl(photo)) {
            GlideImageLoader.loadImage(context, ivSliderImage, R.drawable.xml_default_img, photo)
        } else {
            ivSliderImage.setImageURI(Uri.parse(photo))
        }
        ivSliderImage.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onClickImage(photo)
            }
        }
        container.addView(itemView, 0)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    interface AdapterListener {
        fun onClickImage(photo: String?)
    }
}
