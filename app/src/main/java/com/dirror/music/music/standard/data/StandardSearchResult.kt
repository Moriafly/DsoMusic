package com.dirror.music.music.standard.data

data class StandardSearchResult(
    val songs:List<StandardSongData>,
    val playlist:List<StandardPlaylist>,
    val albums:List<StandardAlbum>,
    val singers:List<StandardSinger>
)
