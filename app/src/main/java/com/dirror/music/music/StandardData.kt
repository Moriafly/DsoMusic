package com.dirror.music.music

data class StandardSongData(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val artists: ArrayList<StandardArtistData>,
)

data class StandardArtistData(
    val id: Long,
    val name: String
)