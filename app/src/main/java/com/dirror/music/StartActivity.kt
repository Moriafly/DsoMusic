package com.dirror.music

import android.content.Context
import android.content.Intent
import com.dirror.music.ui.activity.LocalMusicActivity

fun startLocalMusicActivity(context: Context) {
    context.startActivity(Intent(context, LocalMusicActivity::class.java))
}
