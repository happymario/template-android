package com.mario.template.helper.thirdparty.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

// new since Glide v4
@GlideModule
public final class GlideMyAppModule extends AppGlideModule {
    // leave empty for now

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        //super.applyOptions(context, builder);
        builder.setLogLevel(Log.ERROR);
    }
}