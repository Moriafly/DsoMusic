package com.dirror.music.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * 深色主题工具类
 * @version 1.1.1
 * @author Moriafly
 * @since 2020/8/20
 */
object DarkThemeUtil {

    /**
     * 判断是否开启了深色模式
     */
    fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * 设置深色模式
     */
    fun setDarkTheme(open: Boolean) {
        if (open) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }

}