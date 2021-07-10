package com.victoria.bleled.util.view.text;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

public class CustomTypefaceSpan extends MetricAffectingSpan {
    private Typeface typeface = null;
    public CustomTypefaceSpan(Typeface typeface) {
        super();
        this.typeface = typeface;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(typeface);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
        textPaint.setTypeface(typeface);
    }
}
