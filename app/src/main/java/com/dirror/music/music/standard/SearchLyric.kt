package com.dirror.music.music.standard

import com.dirror.music.music.local.LyricParse
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object SearchLyric {

    fun getLyricString(songData: StandardSongData, success: (String) -> Unit) {
        var url = ""
        when (songData.source) {
            // 网易云
            SOURCE_NETEASE -> {
                url = "http://music.eleuu.com/lyric?id=${songData.id}"
                //url = "$API_FCZBL_VIP/?type=lrc&id=${songData.id}"
            }
            // QQ
            SOURCE_QQ -> {
                url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=${songData.id}&format=json&nobase64=1"
            }
            else -> {
                LyricParse.getLyric(songData.name) {
                    success.invoke(it)
                }
            }
        }

        MagicHttp.OkHttpManager().newGet(url, { response ->
            var lyric = response

            when (songData.source) {
                SOURCE_QQ -> {
                    if (Gson().fromJson(lyric, QQSongLyric::class.java).lyric != null) {
                        lyric = Gson().fromJson(lyric, QQSongLyric::class.java).lyric.toString()
                        success.invoke(lyric)
                    } else {
                        success.invoke("")
                    }
                }
                SOURCE_NETEASE -> {
                    if (Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc != null) {
                        lyric = Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc?.lyric.toString()
                        success.invoke(lyric)
                    } else {
                        success.invoke("")
                    }
                }
            }

        }, {

        })
    }

    data class QQSongLyric(
        val lyric: String?
    )

    data class NeteaseSongLyric(
        val lrc: NeteaseSongLrc?
    )

    data class NeteaseSongLrc(
        val lyric: String?
    )

}
