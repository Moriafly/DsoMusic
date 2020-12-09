package com.dirror.music.music.netease

import com.dirror.music.music.dirror.SearchSong

object SongUrl {

    fun getSongUrl(id: String): String {
        return if (SearchSong.getDirrorSongUrl(id) != "") {
            SearchSong.getDirrorSongUrl(id)
        } else {
            "https://music.163.com/song/media/outer/url?id=${id}.mp3"
        }
    }

}