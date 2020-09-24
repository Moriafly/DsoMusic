package com.dirror.music.music

import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object SearchUtil {
    fun searchMusic(keywords: String, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        val url = "${API_MUSIC_API}/search?keywords=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {
            val searchUtilData = Gson().fromJson(it, SearchUtilData::class.java)

            if (searchUtilData.code == 400) {
                failure.invoke("未找到歌曲")
            } else {
                success.invoke(searchUtilDataToStandardSongDataList(searchUtilData))
            }

        }, {

        })
    }

    fun searchUtilDataToStandardSongDataList(searchUtilData: SearchUtilData): ArrayList<StandardSongData> {
        val standardSongDataList = ArrayList<StandardSongData>()
        val songs = searchUtilData.result.songs
        for (index in 0..songs.lastIndex) {
            val standardSongData = StandardSongData(
                songs[index].id,
                songs[index].name,
                songs[index].album.artist.img1v1Url,
                songs[index].artists
            )
            standardSongDataList.add(standardSongData)
        }
        return standardSongDataList
    }

}

data class SearchUtilData(
    val result: SearchUtilResultData,
    val code: Int
)

data class SearchUtilResultData(
    val songs: ArrayList<SearchUtilSongData>
)

data class SearchUtilSongData(
    val id: Long,
    val name: String,
    val artists: ArrayList<StandardArtistData>,
    val album: SearchUtilAlbumData
)

data class SearchUtilAlbumData(
    val artist: SearchUtilArtistData
)

data class SearchUtilArtistData(
    val img1v1Url: String
)

