package com.victoria.bleled.util.view.font;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.victoria.bleled.R;

import java.util.HashMap;

public class FontMgr {
    public static FontMgr getInstance(Context context) {
        if (instance == null) {
            instance = new FontMgr(context);
        }
        return instance;
    }

    private static FontMgr instance = null;

    private Context context;
    private HashMap<String, Typeface> typefaceHashMap = new HashMap<>();

    public FontMgr(Context context) {
        this.context = context;
    }

    public Typeface getTypeface(EFontName fontname) {
        Typeface typeface = typefaceHashMap.get(fontname.value);
        if (typeface == null) {
            if (fontname == EFontName.main) {
                typeface = ResourcesCompat.getFont(context, R.font.chongbong);
            }

            if (typeface == null) {
                try {
                    typeface = Typeface.createFromAsset(context.getAssets(), "font/" + fontname.value + ".ttf");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (typeface != null) {
                typefaceHashMap.put(fontname.value, typeface);
            }
        }

        return typeface;
    }

    public enum EFontName {
        system(""),
        main("chongbong");

        private String value;

        EFontName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static EFontName getFont(String name) {
            if (name.equals("chongbong")) {
                return EFontName.main;
            }
            return EFontName.system;
        }
    }
}
