package com.dirror.music.music.netease

import com.dirror.music.music.dirror.SearchSong

object SongUrl {

    fun getSongUrl(id: Long): String {
        if (SearchSong.getDirrorSongUrl(id.toString()) != "") {
            return SearchSong.getDirrorSongUrl(id.toString())
        } else {
            return "https://music.163.com/song/media/outer/url?id=${id}.mp3"
        }
    }

}