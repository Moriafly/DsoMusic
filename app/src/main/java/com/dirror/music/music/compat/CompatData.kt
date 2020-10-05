package com.dirror.music.music.compat

import com.dirror.music.music.standard.SOURCE_NETEASE
import com.dirror.music.music.standard.StandardArtistData
import com.dirror.music.music.standard.StandardSongData

// 搜索的解析
data class CompatSearchData(
    val songs: ArrayList<CompatSearchSongData>,
    val code: Int
)

data class CompatSearchSongData(
    val id: Long,
    val name: String,
    val album: CompatAlbumData,
    val artists: ArrayList<CompatArtistData>
)

data class CompatAlbumData(
    val artist: CompatArtistData
)

data class CompatArtistData(
    val id: Long,
    val name: String,
    val img1v1Url: String
)

fun compatSearchDataToStandardPlaylistData(compatSearchData: CompatSearchData): ArrayList<StandardSongData> {
    val standardPlaylistData = ArrayList<StandardSongData>()
    for (song in compatSearchData.songs) {
        val standardArtistDataList = ArrayList<StandardArtistData>()
        // song.artists
        for (index in 0..song.artists.lastIndex) {
            val standardArtistData = StandardArtistData(
                song.artists[index].id,
                song.artists[index].name
            )
            standardArtistDataList.add(standardArtistData)
        }

        val standardSongData = StandardSongData(
            SOURCE_NETEASE,
            song.id,
            song.name,
            song.album.artist.img1v1Url,
            standardArtistDataList
        )
        standardPlaylistData.add(standardSongData)
    }
    return standardPlaylistData
}



