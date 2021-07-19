package com.victoria.bleled.util.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.victoria.bleled.R;
import com.victoria.bleled.databinding.DialogPhotoPagerBinding;
import com.victoria.bleled.util.arch.base.BaseDialog;

import java.util.ArrayList;

public class PhotoPagerDialog extends BaseDialog {
    private DialogPhotoPagerBinding binding;
    private OnClickListener listener;
    private ArrayList<String> photos = new ArrayList<>();
    private PhotoPagerAdapter pagerAdapter;

    public PhotoPagerDialog(Context context, ArrayList<String> photos, int startIdx, OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.photos.addAll(photos);

        initView(startIdx);
    }

    @Override
    public void onCreateView() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_photo_pager, null, false);
        setContentView(this.binding.getRoot());
    }

    private void initView(int startIdx) {
        // ui
        pagerAdapter = new PhotoPagerAdapter(getContext(), photos);
        binding.vpPager.setAdapter(pagerAdapter);
        binding.vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                //updateInfo(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        binding.vpPager.setCurrentItem(startIdx);
        binding.indicator.setViewPager(binding.vpPager);

        // events
        binding.ibBack.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBack();
            }
            dismiss();
        });

        binding.rlBg.setOnClickListener(v -> dismiss());
    }

    public interface OnClickListener {
        void onBack();
    }
}