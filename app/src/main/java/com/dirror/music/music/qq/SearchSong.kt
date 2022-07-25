package com.dirror.music.music.qq

import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object SearchSong {

    fun search(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=20&w=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                var response = it.replace("callback(", "")
                if (response.endsWith(")")) {
                    response = response.substring(0, response.lastIndex)
                }
                val qqSearch = Gson().fromJson(response, QQSearch::class.java)
                val standardSongList = ArrayList<StandardSongData>()
                if (qqSearch.data.song.list.isNotEmpty()) {
                    for (song in qqSearch.data.song.list) {
                        standardSongList.add(song.switchToStandard())
                    }
                }
                success.invoke(standardSongList)
            } catch (e: Exception) {

            }
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
    ) {
        fun switchToStandard(): StandardSongData {
            return StandardSongData(
                SOURCE_QQ,
                songmid,
                songname,
                albummid, // 歌单 id
                singer,
                null,
                null,
                null
            )
        }
    }

}