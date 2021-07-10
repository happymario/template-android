package com.victoria.bleled.util.view.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

import com.victoria.bleled.R;

import java.lang.reflect.Field;

public class NonSwipeViewPager extends ViewPager {
    private static final String TAG = NonSwipeViewPager.class.getSimpleName();

    static class MyScroller extends Scroller {
        private int duration = 700;

        MyScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void startScroll(int i, int i2, int i3, int i4, int i5) {
            super.startScroll(i, i2, i3, i4, this.duration);
        }

        public void startScroll(int i, int i2, int i3, int i4) {
            super.startScroll(i, i2, i3, i4, this.duration);
        }
    }

    public NonSwipeViewPager(Context context) {
        this(context, null);
    }

    public NonSwipeViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NonSwipeViewPager);
        if (obtainStyledAttributes != null) {
            int indexCount = obtainStyledAttributes.getIndexCount();
            int i = 0;
            while (i < indexCount) {
                if (obtainStyledAttributes.getIndex(i) == 0 && obtainStyledAttributes.getBoolean(obtainStyledAttributes.getIndex(i), false)) {
                    m8345a();
                }
                i++;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void next() {
        setCurrentItem(getCurrentItem() + 1, true);
    }

    private void m8345a() {
        try {
            Field declaredField = ViewPager.class.getDeclaredField("mScroller");
            declaredField.setAccessible(true);
            declaredField.set(this, new MyScroller(getContext(), new LinearInterpolator()));
        } catch (Exception e) {
        }
    }
}
