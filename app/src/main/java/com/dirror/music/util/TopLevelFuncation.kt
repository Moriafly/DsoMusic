package com.dirror.music.util

import android.app.Activity
import android.view.Window

/**
 * 顶层函数类
 */

/**
 * 设置状态栏图标颜色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

