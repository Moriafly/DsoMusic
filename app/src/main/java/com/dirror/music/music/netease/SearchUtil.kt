package com.dirror.music.music.netease

import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.music.standard.data.StandardSongData.NeteaseInfo
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object SearchUtil {
    fun searchMusic(keywords: String, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/search?keywords=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {
            val searchUtilData = Gson().fromJson(it, SearchUtilData::class.java)

            if (searchUtilData.code == 400) {
                failure.invoke("未找到歌曲")
            } else {
                if (searchUtilData.msg != null) {
                    failure.invoke("加油！明天会更好！")
                } else {
                    success.invoke(searchUtilDataToStandardSongDataList(searchUtilData))
                }
            }
        }, {

        })
    }

    private fun searchUtilDataToStandardSongDataList(searchUtilData: SearchUtilData): ArrayList<StandardSongData> {
        val standardSongDataList = ArrayList<StandardSongData>()
        searchUtilData.result?.let {
            val songs = it.songs
            for (index in 0..songs.lastIndex) {
                val standardSongData = StandardSongData(
                    SOURCE_NETEASE,
                    songs[index].id.toString(),
                    songs[index].name,
                    songs[index].album.artist.img1v1Url,
                    songs[index].artists,
                    NeteaseInfo(songs[index].fee),
                    null,
                    null
                )
                standardSongDataList.add(standardSongData)
            }
        }

        return standardSongDataList
    }

}

data class SearchUtilData(
    val msg: String?,
    val result: SearchUtilResultData?,
    val code: Int
)

data class SearchUtilResultData(
    val songs: ArrayList<SearchUtilSongData>
)

data class SearchUtilSongData(
    val id: Long,
    val name: String,
    val fee: Int,
    val artists: ArrayList<StandardArtistData>,
    val album: SearchUtilAlbumData
)

data class SearchUtilAlbumData(
    val artist: SearchUtilArtistData
)

data class SearchUtilArtistData(
    val img1v1Url: String
)

