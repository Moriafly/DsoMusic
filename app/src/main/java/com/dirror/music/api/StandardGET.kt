package com.dirror.music.api

import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object StandardGET {
    /**
     *
     */
    fun getMusicBitrate() {

    }

    /**
     * 获取歌曲信息
     *
     * @地址 http://music.eleuu.com/song/url?id=1433167647&br=320000
     */
    fun getSongInfo(id: Long, success: (UrlData) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/url?id=${id}&br=999000"
        MagicHttp.OkHttpManager().newGet(url, {
            val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
            if (songUrlData.code == 200) {
                success.invoke(songUrlData.data[0])
            }
        }, {

        })
    }

    data class SongUrlData(
        val data: ArrayList<UrlData>,
        val code: Int
    )

    data class UrlData(
        val url: String,
        val br: Int,
        val size: Int,
        val type: String?
    )
}