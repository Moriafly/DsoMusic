package com.dirror.music.manager

import android.util.Log
import androidx.annotation.Keep
import com.dirror.music.MyApplication
import com.dirror.music.api.*
import com.dirror.music.data.CommentData
import com.dirror.music.data.UserPlaylistData
import com.dirror.music.manager.interfaces.CloudMusicManagerInterface
import com.dirror.music.music.netease.data.*
import com.dirror.music.util.*
import com.google.gson.Gson

@Keep
class CloudMusicManager: CloudMusicManagerInterface {

    companion object {
        private const val URL_PRIVATE_LETTER = "${API_DEFAULT}/msg/private" // 私信
    }

    /**
     * 时间戳
     */
    fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    /**
     * 返回泛型数据类
     */
//    @Throws(JsonSyntaxException::class)
//    private fun <T> getDataClass(url: String, dataClassOfT: Class<T>?, success: (T) -> Unit, failure: () -> Unit) {
//        MagicHttp.OkHttpManager().newGet(url, {
//            // try {
//            val dataClass = Gson().fromJson(url, dataClassOfT)
//            success.invoke(dataClass)
//            // } catch (e: Exception) {
//            // failure.invoke()
//            // }
//        }, {
//            failure.invoke()
//        })
//    }

    override fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit) {
        val url = "$API_MUSIC_ELEUU/comment/music?id=${id}&limit=20&offset=0${timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val commentData = Gson().fromJson(it, CommentData::class.java)
            success.invoke(commentData)
        }, {

        })
    }

    override fun getUserDetail(userId: Long, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${API_AUTU}/user/detail?uid=${userId}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
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

    override fun getUserDetail(
        uid: String,
        success: (result: com.dirror.music.data.UserDetailData) -> Unit,
        failure: (error: String) -> Unit
    ) {
        MagicHttp.OkHttpManager().newGet("${API_MUSIC_API}/user/detail?uid=$uid", {
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

    override fun loginByTell(tell: String, password: String, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${API_DEFAULT}/login/cellphone?phone=${tell}&password=${password}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                if (userDetail.code != 200) {
                    failure.invoke()
                } else {
                    userDetail.cookie?.let { it1 -> MyApplication.userManager.setCloudMusicCookie(it1) }
                    MyApplication.userManager.setUid(userDetail.profile.userId)
                    success.invoke(userDetail)
                }
            } catch (e: Exception) {
                failure.invoke()
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
    override fun sendComment(
        t: Int,
        type: Int,
        id: String,
        content: String,
        commentId: Long,
        success: (CodeData) -> Unit,
        failure: () -> Unit
    ) {
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

    override fun getPrivateLetter(success: (PrivateLetterData) -> Unit, failure: () -> Unit) {
        val cookie = MyApplication.userManager.getCloudMusicCookie()
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

    override fun getPicture(url: String, heightOrWeight: Int): String {
        return "${url}?param=${heightOrWeight}y${heightOrWeight}"
    }

    override fun getSearchDefault(success: (SearchDefaultData) -> Unit) {
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

    override fun getSearchHot(success: (SearchHotData) -> Unit) {
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

    override fun getArtists(artistId: Long, success: (ArtistsData) -> Unit) {
        val url = CloudMusicApi.ARTISTS + "?id=$artistId"
        MagicHttp.OkHttpManager().newGet(url, {
            val artistsData = Gson().fromJson(it, ArtistsData::class.java)
            if (artistsData.code == 200) {
                success.invoke(artistsData)
            }
        }, {

        })
    }

    override fun getLyric(songId: Long, success: (LyricData) -> Unit) {
        val url = CloudMusicApi.LYRIC + "?id=$songId"
        MagicHttp.OkHttpManager().newGet(url, {
            val lyricData = Gson().fromJson(it, LyricData::class.java)
            if (lyricData .code == 200) {
                success.invoke(lyricData)
            }
        }, {

        })
    }

    override fun getSongInfo(id: String, success: (SongUrlData.UrlData) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/url?id=${id}${timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
            if (songUrlData.code == 200) {
                success.invoke(songUrlData.data[0])
            }
        }, {

        })
    }

    override fun loginByUid(uid: String, success: () -> Unit) {
        getUserDetail(uid, {
            MyApplication.mmkv.encode(Config.UID, it.profile?.userId!!.toLong())
            // UID 登录清空 Cookie
            MyApplication.userManager.setCloudMusicCookie("")
            success.invoke()
            // toast("登录成功${it.profile?.userId!!.toLong()}")
        }, {
            toast(it)
        })
    }

    override fun getUserPlaylist(uid: Long, success: (UserPlaylistData) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_MUSIC_ELEUU}/user/playlist?uid=$uid${timestamp()}", {
            val userPlaylistData = Gson().fromJson(it, UserPlaylistData::class.java)
            success.invoke(userPlaylistData)
        }, {

        })
    }
}