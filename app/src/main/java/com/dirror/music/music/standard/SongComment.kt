package com.dirror.music.music.standard

import android.util.Log
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.CommentData
import com.dirror.music.music.CloudMusic
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object SongComment {

    /**
     * 标准库
     * 获取歌曲评论
     */
    fun getComment(songData: StandardSongData, success: (CommentData) -> Unit, failure: () -> Unit) {
        when (songData.source) {
            SOURCE_NETEASE -> {
                val url = "$API_MUSIC_ELEUU/comment/music?id=${songData.id}&limit=20&offset=0${CloudMusic.timestamp()}"
                MagicHttp.OkHttpManager().newGet(url, {
                    val commentData = Gson().fromJson(it, CommentData::class.java)
                    success.invoke(commentData)
                }, {
                    failure.invoke()
                })
            }
            SOURCE_QQ -> {
                failure.invoke()
            }
        }

    }

}