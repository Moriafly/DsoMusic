package com.dirror.music.service

import android.content.ContentUris
import android.net.Uri
import com.dirror.music.music.kuwo.SearchSong
import com.dirror.music.music.netease.SongUrl
import com.dirror.music.music.qq.PlayUrl
import com.dirror.music.music.standard.data.*

/**
 * 获取歌曲 URL
 */
object ServiceSongUrl {

    inline fun getUrl(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        when (song.source) {
            SOURCE_NETEASE -> {
                success.invoke(SongUrl.getSongUrl(song.id))
            }
            SOURCE_QQ -> {
                PlayUrl.getPlayUrl(song.id) {
                    success.invoke(it)
                }
            }
            SOURCE_LOCAL -> {
                val id = song.id.toLong()
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
                SearchSong.getUrl(song.id) {
                    success.invoke(it)
                }
            }
            else -> success.invoke(null)
        }
    }

}