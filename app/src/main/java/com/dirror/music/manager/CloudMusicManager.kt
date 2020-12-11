package com.dirror.music.manager

import com.dirror.music.MyApplication
import com.dirror.music.api.API_AUTU
import com.dirror.music.api.API_DEFAULT
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.CommentData
import com.dirror.music.music.CloudMusic
import com.dirror.music.music.netease.data.CodeData
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.Config
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

    override fun loginByTell(tell: String, password: String, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${API_DEFAULT}/login/cellphone?phone=${tell}&password=${password}"
        MagicHttp.OkHttpManager().newGet(url, {
            val userDetail = Gson().fromJson(it, UserDetailData::class.java)
            if (userDetail.code != 200) {
                failure.invoke()
            } else {
                MyApplication.userManager.setCloudMusicCookie(userDetail.cookie)
                MyApplication.userManager.setUid(userDetail.profile.userId)
                success.invoke(userDetail)
            }
        }, {
            failure.invoke()
        })
    }

    override fun likeSong(songId: String, success: () -> Unit, failure: () -> Unit) {
        val cookie = MyApplication.userManager.getCloudMusicCookie()
        val url = "${API_DEFAULT}/like?id=${songId}&cookie=${cookie}"
        MagicHttp.OkHttpManager().newGet(url, {
            loge("喜欢音乐返回值：${it}")
            val code = Gson().fromJson(it, CodeData::class.java).code
            if (code != 200) {
                failure.invoke()
            } else {
                success.invoke()
            }
        }, {
            failure.invoke()
        })
    }

}