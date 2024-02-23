package com.mario.lib.base.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import java.util.Locale

object LocaleUtil {
    fun setLocale(c: Context, language: String): Context {
        return updateResources(c, language)
    }

    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (isAtLeastVersion(VERSION_CODES.N)) config.getLocales()[0] else config.locale
    }

    @ChecksSdkIntAtLeast(parameter = 0)
    fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }

    private fun updateResources(context: Context, language: String): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (isAtLeastVersion(VERSION_CODES.N)) {
            setLocaleForApi24(config, locale)
            context = context.createConfigurationContext(config)
        } else if (isAtLeastVersion(VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    @RequiresApi(api = VERSION_CODES.N)
    private fun setLocaleForApi24(config: Configuration, target: Locale) {
        val set: MutableSet<Locale> = LinkedHashSet()
        // bring the target locale to the front of the list
        set.add(target)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            // append other locales supported by the user
            set.add(all[i])
        }
        val locales = set.toTypedArray<Locale>()
        config.setLocales(LocaleList(*locales))
    }
}