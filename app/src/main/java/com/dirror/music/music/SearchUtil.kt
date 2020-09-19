package com.dirror.music.music

import android.os.Parcelable
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.cloudmusic.CommentData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

object SearchUtil {
    fun searchMusic(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val url = "${API_MUSIC_API}/search?keywords=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {
            val searchUtilData = Gson().fromJson(it, SearchUtilData::class.java)
            success.invoke(searchUtilDataToStandardSongDataList(searchUtilData))
        }, {

        })
    }

    private fun searchUtilDataToStandardSongDataList(searchUtilData: SearchUtilData): ArrayList<StandardSongData> {
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
    val result: SearchUtilResultData
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

