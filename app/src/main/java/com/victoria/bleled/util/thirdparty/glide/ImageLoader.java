package com.victoria.bleled.util.thirdparty.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.victoria.bleled.R;

import java.io.File;

public class ImageLoader {

    public static void loadImage(Context context, ImageView view, int defaultResId, String url) {
        loadImage(context, view, url, defaultResId, null);
    }


    public static void loadImage(Context context, ImageView view, String url, int defaultResId, ImageLoadListener callback) {
        if (view == null)
            return;

        try {
            File file = new File(url);
            if (file.exists()) {
                Glide.with(context.getApplicationContext())
                        .load(new File(url))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(defaultResId)
                        .into(view);

                if (callback != null) {
                    callback.onSuccess();
                }
            } else if (url != null && url.isEmpty() == false) {
                Glide.with(context.getApplicationContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                if (defaultResId != 0) {
                                    view.setImageResource(defaultResId);
                                } else {
                                    view.setImageResource(R.color.image_bg);
                                }

                                if (callback != null) {
                                    callback.onError();
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (callback != null) {
                                    callback.onSuccess();
                                }

                                return false;
                            }
                        })
                        .placeholder(defaultResId)
                        .into(view);
            } else {
                if (defaultResId != 0) {
                    Glide.with(context.getApplicationContext())
                            .load(defaultResId)
                            .into(view);
                } else {
                    Glide.with(context.getApplicationContext())
                            .load(R.color.image_bg)
                            .into(view);
                }
            }
        } catch (Exception e) {
            if (callback != null)
                callback.onError();

            if (defaultResId != 0) {
                Glide.with(context.getApplicationContext())
                        .load(defaultResId)
                        .into(view);
            } else {
                Glide.with(context.getApplicationContext())
                        .load(R.color.image_bg)
                        .into(view);
            }
        }
    }


    public interface ImageLoadListener {
        void onSuccess();

        void onError();
    }

    public static class PlayPauseGif extends DrawableImageViewTarget {
        GifDrawable gif = null;

        public PlayPauseGif(ImageView view) {
            super(view);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            if (resource instanceof GifDrawable) {
                gif = (GifDrawable) resource;
            }
            super.onResourceReady(resource, transition);
        }

        public boolean isPlaying() {
            return gif != null && gif.isRunning();
        }

        public void play() {
            if (gif != null) {
                gif.start();
            }
        }

        public void stop() {
            if (gif != null) {
                gif.stop();
            }
        }
    }
}
