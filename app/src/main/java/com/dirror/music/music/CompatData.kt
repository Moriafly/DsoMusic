package com.dirror.music.music
// 搜索的解析
data class CompatSearchData(
    val songs: ArrayList<CompatSearchSongData>,
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
            song.id,
            song.name,
            song.album.artist.img1v1Url,
            standardArtistDataList
        )
        standardPlaylistData.add(standardSongData)
    }
    return standardPlaylistData
}



