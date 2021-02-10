package com.dirror.music.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.dirror.music.MyApplication

/**
 * 深色主题工具类
 * @version 1.1.0
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

    fun setDarkTheme(open: Boolean) {
        MyApplication.mmkv.encode(Config.DARK_THEME, open)
        if (open) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

//    /**
//     * 写入内存数据
//     * 将当前的深色模式状态保持到内存，下次启动 APP 以读取
//     * @param context Context
//     * @param state 深色模式状态，true 打开状态，false 关闭状态
//     */
//    fun writeDarkThemeState(context: Context, state: Boolean) {
//        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
//            .edit{ putBoolean("boolean_dark_theme_state", state) }
//    }
//
//    /**
//     * 读取内存数据
//     */
//    fun readDarkThemeState(context: Context): Boolean {
//        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
//            .getBoolean("boolean_dark_theme_state", false)
//    }

}