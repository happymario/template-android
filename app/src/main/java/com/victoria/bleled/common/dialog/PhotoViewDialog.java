package com.victoria.bleled.common.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.victoria.bleled.R;
import com.victoria.bleled.databinding.DialogPhotoViewBinding;
import com.victoria.bleled.util.arch.base.BaseDialog;
import com.victoria.bleled.util.thirdparty.glide.ImageLoader;

public class PhotoViewDialog extends BaseDialog {
    private DialogPhotoViewBinding binding;
    private OnClickListener listener;
    private String url;

    public PhotoViewDialog(Context context, String url, OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.url = url;

        initView();
    }

    @Override
    public void onCreateView() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_photo_view, null, false);
        setContentView(this.binding.getRoot());
    }

    private void initView() {
        // ui
        ImageLoader.loadImage(context, binding.ivPhoto, R.color.image_bg, url);

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