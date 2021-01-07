package com.dirror.music.music

import android.util.Log
import androidx.annotation.Keep
import com.dirror.music.MyApplication
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.api.API_MUSIC_LAKE
import com.dirror.music.data.*
import com.dirror.music.util.*
import com.google.gson.Gson
import java.lang.Exception

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */
@Keep
@Deprecated("过时，正在迁移")
object CloudMusic {

    fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    /**
     * 通过 UID 登录
     */
    fun loginByUid(uid: String, success: () -> Unit) {
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

    /**
     * 用户歌单
     */
    fun getPlaylist(uid: Long, success: (UserPlaylistData) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_MUSIC_ELEUU}/user/playlist?uid=$uid${timestamp()}", {
            val userPlaylistData = Gson().fromJson(it, UserPlaylistData::class.java)
            success.invoke(userPlaylistData)
        }, {

        })
    }

    /**
     * 获取网易云用户详细信息
     * @param uid 网易云用户 id
     * @param success 成功的回调
     * @param failure 失败的回调
     */
    private fun getUserDetail(uid: String, success: (result: UserDetailData) -> Unit, failure: (error: String) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_MUSIC_API}/user/detail?uid=$uid", {
            try {
                val userDetailData = Gson().fromJson(it, UserDetailData::class.java)
                when (userDetailData.code) {
                    400 -> failure.invoke("获取用户详细信息错误")
                    404 -> failure.invoke("用户不存在")
                    else -> success.invoke(userDetailData)
                }
            } catch (e: Exception) {
                failure.invoke("解析错误")
            }
        }, {
            failure.invoke("MagicHttp 错误\n${it}")
            Log.e("无法连接到服务器", it)
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

    @Deprecated("过时，推荐使用 CloudMusicManager")
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