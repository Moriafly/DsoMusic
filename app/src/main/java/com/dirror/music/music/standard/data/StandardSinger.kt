package com.dirror.music.music.standard.data

data class StandardSinger(
    val id: Long,
    val name: String,
    val picUrl: String,
    val briefDesc: String
)

data class StandardSingerPackage(
    val singer: StandardSinger,
    val songs: List<StandardSongData>
)
