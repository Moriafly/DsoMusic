package com.dirror.music.music.compat

import com.dirror.music.music.standard.data.NeteaseInfo
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData

// 搜索的解析
data class CompatSearchData(
    val songs: ArrayList<CompatSearchSongData>,
    val code: Int
)

data class CompatSearchSongData(
    val id: Long,
    val name: String,
    val al: CompatAlbumData, // val album: CompatAlbumData,
    val ar: ArrayList<CompatArtistData>, // 艺术家
    val fee: Int // 网易云搜索是否是 vip 歌曲，1 为 vip
)

data class CompatAlbumData(
    val picUrl: String,
    // val artist: CompatArtistData
)

data class CompatArtistData(
    val id: Long,
    val name: String,
    // val img1v1Url: String
)

fun compatSearchDataToStandardPlaylistData(compatSearchData: CompatSearchData): ArrayList<StandardSongData> {
    val standardPlaylistData = ArrayList<StandardSongData>()
    for (song in compatSearchData.songs) {
        val standardArtistDataList = ArrayList<StandardArtistData>()
        // song.artists
        for (index in 0..song.ar.lastIndex) {
            val standardArtistData = StandardArtistData(
                song.ar[index].id,
                song.ar[index].name
            )
            standardArtistDataList.add(standardArtistData)
        }

        val standardSongData = StandardSongData(
            SOURCE_NETEASE,
            song.id.toString(),
            song.name,
            song.al.picUrl,
            standardArtistDataList,
            NeteaseInfo(song.fee),
            null
        )
        standardPlaylistData.add(standardSongData)
    }
    return standardPlaylistData
}



