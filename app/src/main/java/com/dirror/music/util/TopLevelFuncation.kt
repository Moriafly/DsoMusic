package com.dirror.music.util

import android.app.Activity
import android.view.Window
import android.widget.Toast
import com.dirror.music.MyApplication

/**
 * 顶层函数类
 */

/**
 * 设置状态栏图标颜色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

fun toast(msg: String) {
    Toast.makeText(MyApplication.context, msg, Toast.LENGTH_SHORT).show()
}

