package com.dirror.music.manager

import android.app.Activity
import android.content.Context

interface ActivityManagerInterface {
    @Deprecated("管理器启动 Activity 过时")
    fun startLoginActivity(context: Context, activity: Activity)

    fun startFeedbackActivity(activity: Activity)
}