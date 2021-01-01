package com.dirror.music.api

import android.graphics.Bitmap
import com.dirror.music.music.CloudMusic
import com.dirror.music.music.qq.Picture
import com.dirror.music.music.standard.data.*
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

/**
 * 标准获取歌曲各种信息类
 */
@Deprecated("弃用")
object StandardGET {

    /**
     * 获取歌曲信息
     * @地址 http://music.eleuu.com/song/url?id=1433167647&br=320000
     */
    fun getSongInfo(id: String, success: (UrlData) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/url?id=${id}${CloudMusic.timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
            if (songUrlData.code == 200) {
                success.invoke(songUrlData.data[0])
            }
        }, {

        })
    }

    /**
     * 获取大图
     * 300
     */
    fun getSongBitmap(song: StandardSongData, success: (Bitmap?) -> Unit) {
        when (song.source) {
            SOURCE_NETEASE -> {
                CloudMusic.getSongImage(song.id) {
                    GlideUtil.load(it) { bitmap ->
                        success.invoke(bitmap)
                    }
                }
            }
            SOURCE_QQ -> {
                GlideUtil.load(Picture.getPictureUrl(song.imageUrl?:"")) { bitmap ->
                    success.invoke(bitmap)
                }
            }
            SOURCE_LOCAL -> {
                success.invoke(null)
            }
            SOURCE_KUWO -> {
                song.imageUrl?.let { url ->
                    GlideUtil.load(url) {
                        success.invoke(it)
                    }
                }
            }
        }
    }

}

data class SongUrlData(
    val data: ArrayList<UrlData>,
    val code: Int
)

data class UrlData(
    val id: Long,
    val url: String?,
    val br: Int,
    val size: Long,
    val type: String?
)