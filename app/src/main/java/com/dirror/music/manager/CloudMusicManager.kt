package com.dirror.music.manager

import android.util.Log
import androidx.annotation.Keep
import com.dirror.music.App
import com.dirror.music.api.*
import com.dirror.music.data.CommentData
import com.dirror.music.music.netease.data.*
import com.dirror.music.util.*
import com.google.gson.Gson

@Deprecated("MVVM 分发到各个模块，避免单例跑")
@Keep
class CloudMusicManager {

    companion object {
        private const val URL_PRIVATE_LETTER = "${API_DEFAULT}/msg/private" // 私信
    }

    /**
     * 时间戳
     */
    private fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit) {
        val url = "$API_AUTU/comment/music?id=${id}&limit=20&offset=0${timestamp()}&cookie=${AppConfig.cookie}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                Log.d("Comment", it)
                val commentData = Gson().fromJson(it, CommentData::class.java)
                if (commentData.code != 200) {
                    toast("根据网易云音乐的调整，Dso Music 需要登录后才能获取评论")
                    failure()
                } else {
                    success(commentData)
                }
            } catch (e: Exception) {
                Log.e("Comment", e.message.toString())
                toast("获取失败或者未登录，根据网易云音乐的调整，Dso Music 需要登录后才能获取评论")
                failure()
            }
        }, {

        })
    }

    fun getUserDetail(userId: Long, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${User.neteaseCloudMusicApi}/user/detail?uid=${userId}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                User.dsoUser.updateFromNet(userDetail)
                User.vipType = userDetail.profile.vipType
                if (userDetail.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(userDetail)
                }
            } catch (e: java.lang.Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    fun getUserDetail(
        uid: String,
        success: (result: com.dirror.music.data.UserDetailData) -> Unit,
        failure: (error: String) -> Unit
    ) {
        MagicHttp.OkHttpManager().newGet("${API_AUTU}/user/detail?uid=$uid", {
            try {
                val userDetailData = Gson().fromJson(it, com.dirror.music.data.UserDetailData::class.java)
                when (userDetailData.code) {
                    400 -> failure.invoke("获取用户详细信息错误")
                    404 -> failure.invoke("用户不存在")
                    else -> success.invoke(userDetailData)
                }
            } catch (e: java.lang.Exception) {
                failure.invoke("解析错误")
            }
        }, {
            failure.invoke("MagicHttp 错误\n${it}")
            Log.e("无法连接到服务器", it)
        })
    }

    fun likeSong(songId: String, success: () -> Unit, failure: () -> Unit) {
        val cookie = AppConfig.cookie
        val url = "${API_DSO}/like?id=${songId}&cookie=${cookie}"
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

    /**
     * 发送评论
     * @param t 1 发送 2 回复
     * @param type 0 歌曲 1 mv 2 歌单 3 专辑 4 电台 5 视频 6 动态
     * @param id 对应资源 id
     * @param content 要发送的内容
     * @param commentId 回复的评论id (回复评论时必填)
     */
    fun sendComment(
        t: Int,
        type: Int,
        id: String,
        content: String,
        commentId: Long,
        success: (CodeData) -> Unit,
        failure: () -> Unit
    ) {
        val cookie = AppConfig.cookie
        var url = "${API_DEFAULT}/comment?t=${t}&type=${type}&id=${id}&content=${content}&cookie=${cookie}"
        if (commentId != 0L) {
            url += "&commentId=${commentId}"
        }
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("评论返回$it")
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

    fun getPrivateLetter(success: (PrivateLetterData) -> Unit, failure: () -> Unit) {
        val cookie = AppConfig.cookie
        val url = "${URL_PRIVATE_LETTER}?cookie=${cookie}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("url:[${url}]私信返回" + it)
                val privateLetterData = Gson().fromJson(it, PrivateLetterData::class.java)
                if (privateLetterData.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(privateLetterData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    fun getPicture(url: String, heightOrWeight: Int): String {
        return "${url}?param=${heightOrWeight}y${heightOrWeight}"
    }

    fun getSearchDefault(success: (SearchDefaultData) -> Unit) {
        val url = CloudMusicApi.SEARCH_DEFAULT
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val searchDefaultData = Gson().fromJson(it, SearchDefaultData::class.java)
                if (searchDefaultData.code == 200) {
                    success.invoke(searchDefaultData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getSearchHot(success: (SearchHotData) -> Unit) {
        val url = CloudMusicApi.SEARCH_HOT_DETAIL
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val searchHotData = Gson().fromJson(it, SearchHotData::class.java)
                if (searchHotData.code == 200) {
                    success.invoke(searchHotData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getArtists(artistId: Long, success: (ArtistsData) -> Unit) {
        val url = CloudMusicApi.ARTISTS + "?id=$artistId"
        MagicHttp.OkHttpManager().newGet(url, {
            val artistsData = Gson().fromJson(it, ArtistsData::class.java)
            if (artistsData.code == 200) {
                success.invoke(artistsData)
            }
        }, {

        })
    }

    fun getLyric(songId: Long, success: (LyricData) -> Unit) {
        val url = CloudMusicApi.LYRIC + "?id=$songId"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val lyricData = Gson().fromJson(it, LyricData::class.java)
                if (lyricData.code == 200) {
                    success.invoke(lyricData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getSongInfo(id: String, success: (SongUrlData.UrlData) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/url?id=${id}${timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
            if (songUrlData.code == 200) {
                success.invoke(songUrlData.data[0])
            }
        }, {

        })
    }

    fun loginByUid(uid: String, success: () -> Unit) {
        getUserDetail(uid, {
            App.mmkv.encode(Config.UID, it.profile?.userId!!.toLong())
            // UID 登录清空 Cookie
            AppConfig.cookie = ""
            success.invoke()
            // toast("登录成功${it.profile?.userId!!.toLong()}")
        }, {
            toast(it)
        })
    }

}