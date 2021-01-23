package com.dirror.music.manager.interfaces

import android.app.Activity
import android.content.Context

interface ActivityManagerInterface {

    fun startLoginActivity(activity: Activity)

    fun startFeedbackActivity(activity: Activity)

    fun startWebActivity(activity: Activity, url: String)

    fun startWebActivity(activity: Activity, url: String, title: String)

    fun startCommentActivity(activity: Activity, source: Int, id: String)

    fun startUserActivity(activity: Activity, userId: Long)

    fun startLoginByPhoneActivity(activity: Activity)

    fun startSettingsActivity(activity: Activity)

    fun startPrivateLetterActivity(activity: Activity)

    fun startPlayerActivity(activity: Activity)

    fun startLoginByUidActivity(activity: Activity)

    fun startPlayHistoryActivity(activity: Activity)

    fun startArtistActivity(activity: Activity, artistId: Long)
}