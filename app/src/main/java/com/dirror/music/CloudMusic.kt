package com.dirror.music

import android.util.Log
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.cloudmusic.*
import com.dirror.music.music.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.StorageUtil
import com.dirror.music.util.getCurrentTime
import com.dirror.music.util.toast
import com.google.gson.Gson

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */

object CloudMusic {
    // api 地址
    private const val MUSIC_API_URL = "http://musicapi.leanapp.cn"
    // https://musicapi.leanapp.cn
    // https://api.fczbl.vip/163/

    private fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }


    fun loginByPhone(phone: String, password: String, callback: LoginCallback) {
        // ${System.currentTimeMillis()}
        val url = "$MUSIC_API_URL/login/cellphone?phone=$phone&password=$password"
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
            "$MUSIC_API_URL/user/playlist?uid=$uid${timestamp()}",
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
        MagicHttp.OkHttpManager().newGet("$MUSIC_API_URL/user/detail?uid=$uid", {
            val userDetailData = Gson().fromJson(it, UserDetailData::class.java)
            when (userDetailData.code) {
                400 -> failure.invoke("获取用户详细信息错误")
                404 -> failure.invoke("用户不存在")
                else -> success.invoke(userDetailData)
            }
        }, {
            failure.invoke("MagicHttp 错误\n${it.message.toString()}")
            Log.e("无法连接到服务器", it.message.toString())
        })
    }

    interface UserDetailCallback {
        fun success(userDetailData: UserDetailData)
        fun failure()
    }

    /**
     * 获取歌单详情
     */
//    fun getDetailPlaylist(id: Long, callback: DetailPlaylistCallback) {
//        MagicHttp.OkHttpManager().get(
//            "$MUSIC_API_URL/playlist/detail?id=$id${timestamp()}",
//            object : MagicHttp.MagicCallback {
//                override fun success(response: String) {
//                    val detailPlaylistData =
//                        Gson().fromJson(response, DetailPlaylistData::class.java)
//                    callback.success(detailPlaylistData)
//                }
//
//                override fun failure(throwable: Throwable) {
//                    Log.e("获取歌单详情错误", throwable.message.toString())
//                }
//            })
//    }
//
//    interface DetailPlaylistCallback {
//        fun success(detailPlaylistData: DetailPlaylistData)
//    }





    /**
     * 获取歌曲详情
     */
    fun getSongDetail(id: Long, success: (StandardSongData) -> Unit) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/song/detail?ids=$id${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val songData = Gson().fromJson(response, SongData::class.java)

                    val standardSongData = StandardSongData(
                        songData.songs[0].id,
                        songData.songs[0].name,
                        songData.songs[0].al.picUrl,
                        songData.songs[0].ar
                    )

                    success.invoke(standardSongData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取歌曲详情错误", throwable.message.toString())
                }
            })
    }

    /**
     * 获取歌曲评论
     * @param id 网易云音乐 id
     * @param success 成功的回调
     */
    fun getMusicComment(id: Long, success: (CommentData) -> Unit) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/comment/music?id=$id&limit=20&offset=0${timestamp()}",
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

}