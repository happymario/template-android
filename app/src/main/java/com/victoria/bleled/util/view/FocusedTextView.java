package com.victoria.bleled.util.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused)
            super.onWindowFocusChanged(focused);
    }


    @Override
    public boolean isFocused() {
        return true;
    }
}