package com.dirror.music.music.netease.data

/**
 * 歌手简单信息和歌曲
 */
data class ArtistsData(
    val artist: ArtistData,
    // val hotSongs: HotSongsData,
    val more: Boolean,
    val code: Int
) {
    data class ArtistData(
        val briefDesc: String,
        val name: String,
        val id: Int
    )

//    data class HotSongsData(
//
//    )
}
