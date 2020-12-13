package com.dirror.music.manager

import android.app.Activity
import android.content.Context

interface ActivityManagerInterface {

    fun startLoginActivity(activity: Activity)

    fun startFeedbackActivity(activity: Activity)

    fun startWebActivity(activity: Activity, url: String)

    fun startCommentActivity(activity: Activity, source: Int, id: String)

    fun startUserActivity(activity: Activity, userId: Long)

    fun startLoginByPhoneActivity(activity: Activity)

    fun startSettingsActivity(activity: Activity)
}