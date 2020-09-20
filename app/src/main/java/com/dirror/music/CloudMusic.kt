package com.dirror.music

import android.util.Log
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.api.API_MUSIC_LAKE
import com.dirror.music.cloudmusic.*
import com.dirror.music.music.StandardSongData
import com.dirror.music.util.*
import com.google.gson.Gson

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */

object CloudMusic {

    private fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    fun loginByPhone(phone: String, password: String, callback: LoginCallback) {
        // ${System.currentTimeMillis()}
        val url = "${API_MUSIC_LAKE}/login/cellphone?phone=$phone&password=$password"
        Log.e("url", url)
        MagicHttp.OkHttpManager().get(
            url,
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    Log.e("返回数据", response)
                    // 成功
                    // 解析 json
                    val loginData = Gson().fromJson(response, LoginData::class.java)
                    // 登录成功
                    MagicHttp.runOnMainThread {
                        when (loginData.code) {
                            200 -> {
                                toast("登录成功\n用户名：${loginData.profile.nickname}")
                                callback.success()
                                StorageUtil.putInt(
                                    StorageUtil.CLOUD_MUSIC_UID,
                                    loginData.profile.userId
                                )
                            }
                            400 -> toast("用户不存在")
                            else -> toast("登录失败\n错误代码：${loginData.code}")
                        }

                    }
                }

                override fun failure(throwable: Throwable) {
                    Log.e("错误", throwable.message.toString())
                }
            })
    }

    interface LoginCallback {
        fun success()
    }

    fun loginByEmail(email: String, password: String) {

    }

    fun loginByUid(uid: Int, callback: LoginByUidCallback) {
        getUserDetail(uid, {
            toast("登录成功")
            callback.success()
            StorageUtil.putInt(StorageUtil.CLOUD_MUSIC_UID, it.profile?.userId!!)
        }, {
            toast(it)
        })
    }

    interface LoginByUidCallback {
        fun success()
    }

    /**
     * 用户歌单
     */
    fun getPlaylist(uid: Int, callback: PlaylistCallback) {
        MagicHttp.OkHttpManager().get(
            "${API_MUSIC_API}/user/playlist?uid=$uid${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val userPlaylistData = Gson().fromJson(response, UserPlaylistData::class.java)
                    callback.success(userPlaylistData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取用户歌单错误", throwable.message.toString())
                }
            })
    }

    interface PlaylistCallback {
        fun success(userPlaylistData: UserPlaylistData)
    }

    /**
     * 获取网易云用户详细信息
     * @param uid 网易云用户 id
     * @param success 成功的回调
     * @param failure 失败的回调
     */
    fun getUserDetail(uid: Int, success: (result: UserDetailData) -> Unit, failure: (error: String) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_MUSIC_API}/user/detail?uid=$uid", {
            val userDetailData = Gson().fromJson(it, UserDetailData::class.java)
            when (userDetailData.code) {
                400 -> failure.invoke("获取用户详细信息错误")
                404 -> failure.invoke("用户不存在")
                else -> success.invoke(userDetailData)
            }
        }, {
            failure.invoke("MagicHttp 错误\n${it}")
            Log.e("无法连接到服务器", it)
        })
    }


    /**
     * 获取歌曲详情
     */
    fun getSongDetail(id: Long, success: (StandardSongData) -> Unit, failure: (String) -> Unit) {
        val url = "${API_MUSIC_API}/song/detail?ids=$id"
        MagicHttp.OkHttpManager().newGet(url, {
            val songData = Gson().fromJson(it, SongData::class.java)

            val standardSongData = StandardSongData(
                songData.songs[0].id,
                songData.songs[0].name,
                songData.songs[0].al.picUrl,
                songData.songs[0].ar
            )

            success.invoke(standardSongData)
        }, {
            failure.invoke(it)
        })

    }

    /**
     * 获取歌曲评论
     * @param id 网易云音乐 id
     * @param success 成功的回调
     */
    fun getMusicComment(id: Long, success: (CommentData) -> Unit) {
        MagicHttp.OkHttpManager().get(
            "${API_MUSIC_API}/comment/music?id=$id&limit=20&offset=0${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val commentData = Gson().fromJson(response, CommentData::class.java)
                    success.invoke(commentData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取歌曲详情错误", throwable.message.toString())
                }
            })
    }

    /**
     * 获取歌曲图片
     * 固定大小 300 * 300
     */
    fun getMusicCoverUrl(musicId: Long): String {
        var url = "$API_FCZBL_VIP/?type=cover&id=$musicId"
        url = url.replace("?param=300y300", "?param=1000y60")
        loge("图片url：${url}")
        return url
    }


    /**
     * 获取歌曲详情
     */
    fun getSongImage(id: Long, success: (String) -> Unit) {
        val url = "${API_MUSIC_API}/song/detail?ids=$id"
        MagicHttp.OkHttpManager().newGet(url, {
            val songData = Gson().fromJson(it, SongData::class.java)
            val imageUrl = songData.songs[0].al.picUrl + "?param=600y600"
            success.invoke(imageUrl)
        }, {

        })

    }

}