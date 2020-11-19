package com.dirror.music.music

import android.util.Log
import com.dirror.music.MyApplication
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.api.API_MUSIC_LAKE
import com.dirror.music.data.*
import com.dirror.music.music.standard.SOURCE_NETEASE
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.util.*
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly
import java.lang.Exception

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */
object CloudMusic {

    fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    fun loginByPhone(phone: String, password: String, success: () -> Unit) {
        val url = "${API_MUSIC_LAKE}/login/cellphone?phone=$phone&password=$password"
        MagicHttp.OkHttpManager().newGet(url, { response ->
            try {
                val loginData = Gson().fromJson(response, LoginData::class.java)
                MagicHttp.runOnMainThread {
                    when (loginData.code) {
                        200 -> {
                            toast("登录成功\n用户名：${loginData.profile.nickname}")
                            success.invoke()
                            StorageUtil.putLong(
                                StorageUtil.CLOUD_MUSIC_UID,
                                loginData.profile.userId.toLong()
                            )
                        }
                        400 -> toast("用户不存在")
                        else -> toast("登录失败\n错误代码：${loginData.code}")
                    }
                }
            } catch (e: Exception) {
                toast("API $API_MUSIC_LAKE 连接失败")
            }
        }, {
            Log.e("错误", it)
        })
    }

    @TestOnly
    fun loginByEmail(email: String, password: String) {

    }

    /**
     * 通过 UID 登录
     */
    fun loginByUid(uid: Long, success: () -> Unit) {
        getUserDetail(uid, {
            success.invoke()
            MyApplication.mmkv.encode(Config.UID, it.profile?.userId!!.toLong())
            toast("登录成功")
        }, {
            toast(it)
        })
    }

    /**
     * 用户歌单
     */
    fun getPlaylist(uid: Long, success: (UserPlaylistData) -> Unit) {
        MagicHttp.OkHttpManager().get(
            "${API_MUSIC_ELEUU}/user/playlist?uid=$uid${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val userPlaylistData = Gson().fromJson(response, UserPlaylistData::class.java)
                    success.invoke(userPlaylistData)
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
    fun getUserDetail(uid: Long, success: (result: UserDetailData) -> Unit, failure: (error: String) -> Unit) {
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
    @TestOnly
    fun getSongDetail(id: Long, success: (StandardSongData) -> Unit, failure: (String) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/detail?ids=$id"
        MagicHttp.OkHttpManager().newGet(url, {
            val songData = Gson().fromJson(it, SongData::class.java)

            val standardSongData = StandardSongData(
                SOURCE_NETEASE,
                songData.songs[0].id.toString(),
                songData.songs[0].name?:"",
                songData.songs[0].al.picUrl,
                songData.songs[0].ar,
                null
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
    fun getMusicComment(id: String, success: (CommentData) -> Unit) {
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
    @Deprecated("推荐使用标准获取，分网易云和 QQ 版本")
    fun getMusicCoverUrl(musicId: Any): String {
        if (musicId is Long) {
            var url = "$API_FCZBL_VIP/?type=cover&id=$musicId"
            url = url.replace("?param=300y300", "?param=1000y60")
            loge("图片url：${url}")
            return url
        }
        return ""
    }


    /**
     * 获取歌曲图片
     */
    fun getSongImage(id: String, success: (String) -> Unit) {
        val url = "${API_MUSIC_API}/song/detail?ids=$id"
        MagicHttp.OkHttpManager().newGet(url, {
            val songData = Gson().fromJson(it, SongData::class.java)
            val imageUrl = songData.songs[0].al.picUrl + "?param=600y600"
            success.invoke(imageUrl)
        }, {

        })

    }

    /**
     * 获取登录状态
     */
    fun getLoginStatus(success: (String) -> Unit) {
        val url = "${API_MUSIC_LAKE}/login/status"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val loginStatusData = Gson().fromJson(it, LoginStatusData::class.java)
                // success.invoke()
                runOnMainThread {
                    if (loginStatusData.code == 200) {
                        toast("登录状态：已经登录")
                    } else {
                        // toast("code:${loginStatusData.code}msg:${loginStatusData.msg}")
                    }
                }
            } catch (e: Exception) {
                toast("API $API_MUSIC_LAKE 连接失败")
            }
        }, {

        })
        // https://musiclake.leanapp.cn/login/status
    }

    data class LoginStatusData(
        val code: Int,
        val msg: String
    )

    fun likeSong(musicId: String) {
        val url = "${API_MUSIC_LAKE}/like?id=$musicId"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val code = Gson().fromJson(it, CodeData::class.java).code
                runOnMainThread {
                    if (code == 200) {
                        toast("歌曲成功添加到我喜欢")
                    } else {
                        toast("歌曲添加到我喜欢失败")
                    }
                }
            } catch (e: Exception) {
                toast("API $API_MUSIC_LAKE 连接失败")
            }

        }, {

        })
        //https://musiclake.leanapp.cn/like?id=1423062698
    }

    data class CodeData(
        val code: Int
    )



}