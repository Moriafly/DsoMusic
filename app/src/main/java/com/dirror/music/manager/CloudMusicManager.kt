package com.dirror.music.manager

import com.dirror.music.api.API_AUTU
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.CommentData
import com.dirror.music.music.CloudMusic
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson

class CloudMusicManager: CloudMusicManagerInterface {

    override fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit) {
        val url = "$API_MUSIC_ELEUU/comment/music?id=${id}&limit=20&offset=0${CloudMusic.timestamp()}"
        loge("评论 url：$url")
        MagicHttp.OkHttpManager().newGet(url, {
            val commentData = Gson().fromJson(it, CommentData::class.java)
            success.invoke(commentData)
        }, {
            failure.invoke()
        })
    }

    override fun getUserDetail(userId: Long, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${API_AUTU}/user/detail?uid=${userId}"
        MagicHttp.OkHttpManager().newGet(url, {
            val userDetail = Gson().fromJson(it, UserDetailData::class.java)
            if (userDetail.code != 200) {
                failure.invoke()
            } else {
                success.invoke(userDetail)
            }
        }, {
            failure.invoke()
        })
    }

}