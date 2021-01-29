package com.dirror.music.music.compat

import com.dirror.music.music.standard.data.StandardSongData.NeteaseInfo
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData

// 搜索的解析
data class CompatSearchData(
    val songs: ArrayList<CompatSearchSongData>,
    val privileges: ArrayList<PrivilegesData>,
    val code: Int
) {
    data class CompatSearchSongData(
        val id: Long,
        val name: String,
        val al: CompatAlbumData, // val album: CompatAlbumData,
        val ar: ArrayList<CompatArtistData>, // 艺术家
        val fee: Int // 网易云搜索是否是 vip 歌曲
    ) {
        /**
         * 专辑
         */
        data class CompatAlbumData(
            val picUrl: String,
            // val artist: CompatArtistData
        )

        data class CompatArtistData(
            val id: Long,
            val name: String,
            // val img1v1Url: String
        )
    }

    data class PrivilegesData(
        val pl: Int,
        val maxbr: Int,
        val flag: Int
    )
}

fun compatSearchDataToStandardPlaylistData(compatSearchData: CompatSearchData): ArrayList<StandardSongData> {
    val standardPlaylistData = ArrayList<StandardSongData>()
    for ((index, song) in compatSearchData.songs.withIndex()) {
        val standardArtistDataList = ArrayList<StandardArtistData>()
        // song.artists
        for (i in 0..song.ar.lastIndex) {
            val standardArtistData = StandardArtistData(
                song.ar[i].id,
                song.ar[i].name
            )
            standardArtistDataList.add(standardArtistData)
        }

        val privileges = compatSearchData.privileges[index]

        val standardSongData = StandardSongData(
            SOURCE_NETEASE,
            song.id.toString(),
            song.name,
            song.al.picUrl,
            standardArtistDataList,
            NeteaseInfo(
                song.fee,
                privileges.pl,
                privileges.flag,
                privileges.maxbr
                ),
            null,
            null
        )
        standardPlaylistData.add(standardSongData)
    }
    return standardPlaylistData
}



