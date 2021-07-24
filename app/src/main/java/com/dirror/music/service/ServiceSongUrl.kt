package com.dirror.music.service

import android.content.ContentUris
import android.net.Uri
import android.text.TextUtils
import com.dirror.music.MyApp
import com.dirror.music.data.LyricViewData
import com.dirror.music.music.kuwo.SearchSong
import com.dirror.music.music.netease.SongUrl
import com.dirror.music.music.qq.PlayUrl
import com.dirror.music.music.standard.SearchLyric
import com.dirror.music.music.standard.data.*
import com.dirror.music.util.runOnMainThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 获取歌曲 URL
 */
object ServiceSongUrl {

    inline fun getUrl(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        when (song.source) {
            SOURCE_NETEASE -> {
                GlobalScope.launch {
                    success.invoke(SongUrl.getSongUrlN(song.id?:""))
                }
            }
            SOURCE_QQ -> {
                PlayUrl.getPlayUrl(song.id?:"") {
                    success.invoke(it)
                }
            }
            SOURCE_LOCAL -> {
                val id = song.id?.toLong() ?: 0
                val contentUri: Uri =
                    ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                success.invoke(contentUri)
            }
            SOURCE_DIRROR -> {
                song.dirrorInfo?.let {
                    success.invoke(it.url)
                }
            }
            SOURCE_KUWO -> {
                SearchSong.getUrl(song.id?:"") {
                    success.invoke(it)
                }
            }
            SOURCE_NETEASE_CLOUD -> {
                SongUrl.getSongUrlCookie(song.id?:"") {
                    success.invoke(it)
                }
            }
            else -> success.invoke(null)
        }
    }

    fun getLyric(song: StandardSongData, success: (LyricViewData) -> Unit) {
        if (song.source == SOURCE_NETEASE) {
            MyApp.cloudMusicManager.getLyric(song.id?.toLong() ?: 0) { lyric ->
                runOnMainThread {
                    val l = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                    success.invoke(l)
                }
            }
        } else {
            SearchLyric.getLyricString(song) { string ->
                runOnMainThread {
                    success.invoke(LyricViewData(string, ""))
                }
            }
        }
    }

}