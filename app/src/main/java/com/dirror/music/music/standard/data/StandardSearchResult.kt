package com.dirror.music.music.standard.data

import com.dirror.music.data.Playlist

data class StandardSearchResult(
    val songs:List<StandardSongData>,
    val playlist:List<StandardPlaylist>
)