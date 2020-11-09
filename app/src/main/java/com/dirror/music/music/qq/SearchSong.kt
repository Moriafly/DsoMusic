package com.dirror.music.music.qq

import com.dirror.music.music.standard.SOURCE_QQ
import com.dirror.music.music.standard.StandardArtistData
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.parseArtist
import com.google.gson.Gson
import java.lang.reflect.Array

object SearchSong {

    fun search(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=20&w=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {
            var response = it.replace("callback(", "")
            if (response.endsWith(")")) {
                response = response.substring(0, response.lastIndex)
            }
            val qqSearch = Gson().fromJson(response, QQSearch::class.java)
            val standardSongList = ArrayList<StandardSongData>()
            for (song in qqSearch.data.song.list) {
                standardSongList.add(
                    StandardSongData(
                    SOURCE_QQ,
                        song.songmid,
                        song.songname,
                        song.albummid,
                        song.singer
                    )
                )
            }
            success.invoke(standardSongList)
        }, {

        })
    }

    data class QQSearch(
        val data: QQSearchData
    )

    data class QQSearchData(
        val song: QQSearchSongList
    )

    data class QQSearchSongList(
        val list: ArrayList<QQSearchSong>
    )

    data class QQSearchSong(
        val albummid: String,
        val songname: String, // 歌名
        val songmid: String,
        val singer: ArrayList<StandardArtistData>
    )

}