package com.dirror.music.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity

/**
 * 最新歌曲适配器
 * @版本 2.7.0-alpha 设计
 */
class SongAdapter {

}

/**
 * 播放音乐
 */
fun playMusic(context: Context, songData: StandardSongData, songDataList: ArrayList<StandardSongData>) {
    // 获取 position
    val position = if (songDataList.indexOf(songData) == -1) {
        0
    } else {
        songDataList.indexOf(songData)
    }
    // 歌单相同
    if (MyApplication.musicController.value?.getPlaylist() == songDataList) {
        // position 相同
        if (position == MyApplication.musicController.value?.getNowPosition()) {
            context.startActivity(Intent(context, PlayerActivity::class.java))
            (context as Activity).overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        } else {
            MyApplication.musicController.value?.playMusic(songData)
        }
    } else {
        // 设置歌单
        MyApplication.musicController.value?.setPlaylist(songDataList)
        // 播放歌单
        MyApplication.musicController.value?.playMusic(songData)
    }
}