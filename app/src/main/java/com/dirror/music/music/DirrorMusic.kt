package com.dirror.music.music

import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.api.API_MUSIC_LAKE
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge

/**
 * Dirror 音乐库
 * 基于网易云云盘构建
 * http://music.eleuu.com/song/url?id=1433183123 // 这是我上传的歌曲，可以获取到
 *
 */
object DirrorMusic {

    fun getSongDetail(id: Long, success: (StandardSongData) -> Unit) {
        val url = "${API_MUSIC_API}/song/url?id=1433183123"
        MagicHttp.OkHttpManager().newGet(url, {
            loge(it)
        }, {

        })
    }

}