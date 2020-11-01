package com.dirror.music.util

import android.content.res.Resources
import android.util.DisplayMetrics
import com.dirror.music.MyApplication
import java.util.*


object LanguageUtils {

    fun setLanguage(id: Int) {
        toast("设置语言${id}")
        when (id) {
            0 -> updateLanguage(Locale.getDefault())
            1 -> updateLanguage(Locale.ENGLISH)
            else -> updateLanguage(Locale.getDefault())
        }
    }

    private fun updateLanguage(locale: Locale) {
        //获取res对象
        val resources: Resources = MyApplication.context.resources
        //获得设置对象
        val config = resources.configuration
        //语言
        config.setLocale(locale)
        // MyApplication.context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)




    }
}