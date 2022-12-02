package com.victoria.bleled.app.dialog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.victoria.bleled.R;
import com.victoria.bleled.util.CommonUtil;
import com.victoria.bleled.util.thirdparty.glide.ImageLoader;
import com.victoria.bleled.util.view.imageview.TouchImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotoPagerAdapter extends PagerAdapter {
    Context context;
    List<String> photos = new ArrayList<>();
    private AdapterListener listener;

    PhotoPagerAdapter(Context context, List<String> modelPhotos) {
        this.context = context;
        this.photos = modelPhotos;
    }

    public void setListener(AdapterListener adapterListener) {
        listener = adapterListener;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_pager_photo_zoom, null);
        TouchImageView ivSliderImage = (TouchImageView) itemView.findViewById(R.id.iv_photo);
        ivSliderImage.setZoom(0.99f);
        ivSliderImage.resetZoom();

        String photo = photos.get(position);

        if (CommonUtil.isValidUrl(photo)) {
            ImageLoader.loadImage(context, ivSliderImage, R.drawable.xml_default_img, photo);
        } else {
            ivSliderImage.setImageURI(Uri.parse(photo));
        }

        ivSliderImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickImage(photo);
            }
        });

        container.addView(itemView, 0);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    public interface AdapterListener {
        void onClickImage(String photo);
    }
}
