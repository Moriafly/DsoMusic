package com.dirror.music.manager

import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.CommentData
import com.dirror.music.music.CloudMusic
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

class CloudMusicManager: CloudMusicManagerInterface {

    override fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit) {
        val url = "$API_MUSIC_ELEUU/comment/music?id=${id}&limit=20&offset=0${CloudMusic.timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val commentData = Gson().fromJson(it, CommentData::class.java)
            success.invoke(commentData)
        }, {
            failure.invoke()
        })
    }

}