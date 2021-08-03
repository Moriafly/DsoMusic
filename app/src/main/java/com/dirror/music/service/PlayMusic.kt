package com.dirror.music.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dirror.music.MyApp
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.player.PlayerActivity
import com.dirror.music.util.Api
import com.dirror.music.util.Config
import com.dirror.music.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

/**
 * 播放音乐
 */
private fun playMusicInternal(context: Context?, song: StandardSongData, songList: ArrayList<StandardSongData>) {
    // MyApp.musicController.value?.setPersonFM(false)
    // 获取 position
    val position = if (songList.indexOf(song) == -1) {
        0
    } else {
        songList.indexOf(song)
    }
    // 歌单相同
    if (MyApp.musicController.value?.getPlaylist() == songList) {
        // position 相同
        if (position == MyApp.musicController.value?.getNowPosition() && context is Activity) {
            context.startActivity(Intent(context, PlayerActivity::class.java))
            context.overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        } else {
            MyApp.musicController.value?.playMusic(song)
        }
    } else {
        // 设置歌单
        MyApp.musicController.value?.setPlaylist(songList)
        // 播放歌单
        MyApp.musicController.value?.playMusic(song)
    }
}

fun playMusic(context: Context?, song: StandardSongData, songList: ArrayList<StandardSongData>) {
    playMusic(context, song, songList, false)
}

fun playMusic(context: Context?, song: StandardSongData, songList: ArrayList<StandardSongData>, forcePlay: Boolean) {
    if (song.neteaseInfo?.pl == 0) {
        GlobalScope.launch {
            var comsumed = false
            if (MyApp.mmkv.decodeBool(Config.AUTO_CHANGE_RESOURCE, false)) {
                val other =  Api.getOtherCPSong(song)
                withContext(Dispatchers.Main) {
                    if (other != null) {
                        other.imageUrl = song.imageUrl
                        val index = songList.indexOf(song)
                        if (index >= 0) {
                            songList.removeAt(index)
                            songList.add(index, other)
                        }
                        playMusicInternal(context, other, songList)
                        val sourceName = when (other.source) {
                            5 -> "酷我"
                            3 -> "QQ"
                            else -> ""
                        }
                        toast("替换为${sourceName}音源成功")
                        comsumed = true
                    } else if (forcePlay) {//通过取串错误切歌,防止中断播放队列
                        comsumed = true
                        playMusicInternal(context, song, songList)
                    }
                }
            }
            if (!comsumed) {
                withContext(Dispatchers.Main) {
                    toast("网易云暂无版权或者是 VIP 歌曲，可以试试 QQ 音源")
                }
            }

        }
    } else {
        playMusicInternal(context, song, songList)
    }
}