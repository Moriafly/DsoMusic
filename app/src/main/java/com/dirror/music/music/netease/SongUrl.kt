package com.dirror.music.music.netease

import com.dirror.music.music.dirror.SearchSong

object SongUrl {

    fun getSongUrl(id: String): String {
        if (SearchSong.getDirrorSongUrl(id) != "") {
            return SearchSong.getDirrorSongUrl(id)
        } else {
            return "https://music.163.com/song/media/outer/url?id=${id}.mp3"
        }
    }

}