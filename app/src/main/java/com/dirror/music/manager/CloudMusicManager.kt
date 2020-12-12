package com.dirror.music.manager

import com.dirror.music.MyApplication
import com.dirror.music.api.API_AUTU
import com.dirror.music.api.API_DEFAULT
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.CommentData
import com.dirror.music.music.CloudMusic
import com.dirror.music.music.netease.data.BannerData
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
            try {
                loge("喜欢音乐返回值：${it}")
                val code = Gson().fromJson(it, CodeData::class.java).code
                if (code != 200) {
                    failure.invoke()
                } else {
                    success.invoke()
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }


    override fun getBanner(success: (BannerData) -> Unit, failure: () -> Unit) {
        val url = "${API_DEFAULT}/banner?type=2"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val bannerData = Gson().fromJson(it, BannerData::class.java)
                if (bannerData.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(bannerData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    /**
     * 发送评论
     * @param t 1 发送 2 回复
     * @param type 0 歌曲 1 mv 2 歌单 3 专辑 4 电台 5 视频 6 动态
     * @param id 对应资源 id
     * @param content 要发送的内容
     * @param commentId 回复的评论id (回复评论时必填)
     */
    override fun sendComment(t: Int, type: Int, id: String, content: String, commentId: Long,success: (CodeData) -> Unit, failure: () -> Unit) {
        val cookie = MyApplication.userManager.getCloudMusicCookie()
        var url = "${API_DEFAULT}/comment?t=${t}&type=${type}&id=${id}&content=${content}&cookie=${cookie}"
        if (commentId != 0L) {
            url += "&commentId=${commentId}"
        }
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("评论返回" + it)
                val codeData = Gson().fromJson(it, CodeData::class.java)
                if (codeData.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(codeData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }


}