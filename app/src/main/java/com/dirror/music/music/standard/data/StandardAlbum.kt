package com.dirror.music.music.standard.data

data class StandardAlbum (
    val name: String,
    val id: Long,
    val size: Int,
    val picUrl: String,
    val publishTime: Long,
    val company: String,
    val artName:String,
    val description:String
)

data class StandardAlbumPackage(
    val album: StandardAlbum,
    val songs: List<StandardSongData>
)