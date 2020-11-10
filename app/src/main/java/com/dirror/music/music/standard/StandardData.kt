package com.dirror.music.music.standard

const val SOURCE_NATIVE = 0
const val SOURCE_NETEASE = 1
const val SOURCE_QQ = 2

val EMPTY_STANDARD_SONG = StandardSongData(
    SOURCE_NATIVE,
    -1L,
    "",
    "",
    ArrayList()
)

data class StandardSongData(
    val source: Int, // 歌曲来源，网易，QQ，本地
    val id: Any?,
    val name: String,
    val imageUrl: String?,
    val artists: ArrayList<StandardArtistData>?,
)

data class StandardArtistData(
    val id: Long?,
    val name: String?
)