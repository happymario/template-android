package com.mario.template.helper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate


object ThemeHelper {
    public const val LIGHT_MODE = "light"
    public const val DARK_MODE = "dark"
    public const val DEFAULT_MODE = "default"

    fun isDarkThemeOn(context: Context): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    fun applyTheme(@NonNull themePref: String) {
        when (themePref) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }
}