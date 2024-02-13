package com.mario.lib.base.view.viewpager;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class NoAnimationPageTransformer implements ViewPager.PageTransformer {
    public void transformPage(View view, float f) {
        if (f < 0.0f || f > 0.0f) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
