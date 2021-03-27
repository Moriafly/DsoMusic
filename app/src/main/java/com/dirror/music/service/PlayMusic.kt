package com.dirror.music.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity

/**
 * 播放音乐
 */
fun playMusic(context: Context?, song: StandardSongData, songList: ArrayList<StandardSongData>) {
    // 获取 position
    val position = if (songList.indexOf(song) == -1) {
        0
    } else {
        songList.indexOf(song)
    }
    // 歌单相同
    if (MyApplication.musicController.value?.getPlaylist() == songList) {
        // position 相同
        if (position == MyApplication.musicController.value?.getNowPosition()) {
            if (context != null) {
                context.startActivity(Intent(context, PlayerActivity::class.java))
                (context as Activity).overridePendingTransition(
                    R.anim.anim_slide_enter_bottom,
                    R.anim.anim_no_anim
                )
            }
        } else {
            MyApplication.musicController.value?.playMusic(song)
        }
    } else {
        // 设置歌单
        MyApplication.musicController.value?.setPlaylist(songList)
        // 播放歌单
        MyApplication.musicController.value?.playMusic(song)
    }
}