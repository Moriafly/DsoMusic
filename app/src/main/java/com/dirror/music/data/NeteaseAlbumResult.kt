package com.dirror.music.data

import com.dirror.music.music.standard.data.StandardSongData

data class NeteaseAlbumResult(
    val code:Int ,
    val songs:List<Song>,
    val album:Album
) {
    fun switchToStandardSongs():List<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        songs.forEach{
            list.add(it.switchToStandard())
        }
        return list
    }
}


