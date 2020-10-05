package com.dirror.music.music.standard

const val SOURCE_NATIVE = 0
const val SOURCE_NETEASE = 1
const val SOURCE_QQ = 1

data class StandardSongData(
    val source: Int, // S
    val id: Long?,
    val name: String,
    val imageUrl: String?,
    val artists: ArrayList<StandardArtistData>?,
)

data class StandardArtistData(
    val id: Long?,
    val name: String?
)