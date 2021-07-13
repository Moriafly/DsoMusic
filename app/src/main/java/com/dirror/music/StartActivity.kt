package com.dirror.music

import android.content.Context
import android.content.Intent
import com.dirror.music.ui.activity.LocalMusicActivity
import com.dirror.music.ui.activity.UserActivity

fun startUserActivity(context: Context, uid: Long) {
    val intent = Intent(context, UserActivity::class.java)
    intent.putExtra(UserActivity.EXTRA_LONG_USER_ID, uid)
    context.startActivity(intent)
}

fun startLocalMusicActivity(context: Context) {
    context.startActivity(Intent(context, LocalMusicActivity::class.java))
}
