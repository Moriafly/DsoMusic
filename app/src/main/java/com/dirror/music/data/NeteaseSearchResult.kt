package com.dirror.music.data

import com.dirror.music.music.standard.data.*

class NeteaseSearchResult(
    val code: Int,
    val result: Result
)

data class Result(
    val songCount: Int,
    val songs: List<Song>?,
    val playlists: List<Playlist>?,
    val playlistCount: Int
) {
    fun switchToStandardSongs():List<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        if (songs != null) {
            for (song in songs) {
                list.add(song.switchToStandard())
            }
        }
        return list
    }

    fun switchToStandardPlaylist():List<StandardPlaylist> {
        val list = ArrayList<StandardPlaylist>()
        if (playlists != null) {
            for (playlist in playlists) {
                list.add(playlist.toStandardPlaylist())
            }
        }
        return list
    }

    fun toStandardResult(): StandardSearchResult {
        return StandardSearchResult(switchToStandardSongs(), switchToStandardPlaylist())
    }
}

data class Playlist(
    val id: Long,
    val name: String,
    val coverImgUrl: String?,
    val playCount: Long,
    val description: String?,
    val trackCount: Int,
    val creator: Creator
) {
    fun toStandardPlaylist(): StandardPlaylist {
        return StandardPlaylist(id,name, coverImgUrl?:"",description?:"", creator.nickname?:"", trackCount, playCount)
    }
}

data class Creator(
    val userId: Long,
    val nickname: String
)

data class Song(
    val al: Al?,
    val ar: List<Ar>,
    val copyright: Long,
    val fee: Int,
    val id: String,
    val name: String,
    val privilege: Privilege?,
) {
    fun switchToStandard():StandardSongData {
        return StandardSongData(SOURCE_NETEASE, id, name, al?.picUrl, getArtList(), getNeteaseInfo(), null, null)
    }

    private fun getNeteaseInfo(): StandardSongData.NeteaseInfo {
        return StandardSongData.NeteaseInfo(fee, 1, 0, privilege?.maxbr)
    }

    private fun getArtList():ArrayList<StandardSongData.StandardArtistData> {
        val list = ArrayList<StandardSongData.StandardArtistData>()
        for (art in ar) {
            list.add(art.switchToStandard())
        }
        return list
    }
}

data class Al(
    val id: Long,
    val name: String,
    val pic: Long,
    val picUrl: String,
)

data class Ar(
    val id: Long,
    val name: String,
) {
    fun switchToStandard():StandardSongData.StandardArtistData{
        return StandardSongData.StandardArtistData(id, name)
    }
}

data class Privilege(
    val maxbr: Int
)
