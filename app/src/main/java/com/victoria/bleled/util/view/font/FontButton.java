package com.victoria.bleled.util.view.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatButton;

import com.victoria.bleled.R;
import com.victoria.bleled.util.CommonUtil;


public class FontButton extends AppCompatButton {
    private float defaultFontSize = 0;
    private float multiplier = 1.0f;

    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        defaultFontSize = a.getDimensionPixelSize(R.styleable.FontTextView_defaultTextSize, (int) getTextSize());
        defaultFontSize = CommonUtil.pxToDp(ctx, (int) defaultFontSize);
        a.recycle();
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;

        float fontsize = multiplier * defaultFontSize;
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
    }

    public boolean setCustomFont(String fontName) {
        FontMgr fontMgr = FontMgr.getInstance(getContext());
        Typeface typeface = fontMgr.getTypeface(FontMgr.EFontName.getFont(fontName));
        if (typeface != null) {
            setTypeface(typeface);
            return true;
        }

        setTypeface(null);
        return false;
    }

}
