package com.dirror.music.music.standard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.dirror.music.R
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.music.local.LocalMusic
import com.dirror.music.music.standard.data.*
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.extensions.dp
import com.dirror.music.util.loge
import org.jetbrains.annotations.TestOnly

object SongPicture {

    const val TYPE_LARGE = 1
    const val TYPE_SMALL = 2

    /**
     * 标准
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    // @Deprecated("过时，等待新方法")
    fun getSongPicture(context: Context, songData: StandardSongData, type: Int, success: (Bitmap) -> Unit) {
        // 普通背景
        if (songData.source == SOURCE_LOCAL) {
            songData.imageUrl?.let {
                LocalMusic.getBitmapFromUir(context, it.toUri())?.let { it1 ->
                    success(it1)
                }
            }
        } else {
            val url = getSongPictureUrl(songData, type)
            GlideUtil.load(url) {
                success.invoke(it)
            }
        }
    }

    fun getMiniPlayerSongPicture(songData: StandardSongData): String? {
        return if (songData.source == SOURCE_LOCAL) {
            songData.imageUrl
        } else {
            getSongPictureUrl(songData, TYPE_SMALL)
        }
    }

    /**
     * 获取图片
     */
    private fun getSongPictureUrl(songData: StandardSongData, type: Int): String {
        return when (songData.source) {
            SOURCE_NETEASE -> {
                // url = url.replace("?param=300y300", "?param=1000y60")
                when (type) {
                    TYPE_LARGE -> {
                        // "${songData.imageUrl}?param=300y300"
                        "$API_FCZBL_VIP/?type=cover&id=${songData.id}&param=${240.dp()}y${240.dp()}"
                    }
                    else -> {
                        "$API_FCZBL_VIP/?type=cover&id=${songData.id}&param=${40.dp()}y${40.dp()}"
                    }
                }

            }
            SOURCE_QQ -> {
                "https://y.gtimg.cn/music/photo_new/T002R300x300M000${songData.imageUrl as String}.jpg?max_age=2592000"
            }
            SOURCE_KUWO -> {
                songData.imageUrl ?: ""
            }
            else -> {
                "https://s4.music.126.net/style/web2/img/default/default_album.jpg"
            }
        }

    }

    /**
     * 获取 PlayerActivityCover 图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @TestOnly
    fun getPlayerActivityCoverBitmap(context: Context, songData: StandardSongData, size: Int, success: (Bitmap) -> Unit) {
        when (songData.source) {
            SOURCE_NETEASE -> {
                loge("getPlayerActivityCoverBitmap网易云图片原url【${songData.imageUrl}】")
                val url = if (songData.imageUrl == "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                    || songData.imageUrl == "https://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                ) {
                    "$API_FCZBL_VIP/?type=cover&id=${songData.id}"
                } else {
                    "${songData.imageUrl}?param=${size}y${size}"
                }
                loge("getPlayerActivityCoverBitmap网易云图片url【${songData.imageUrl}?param=${size}y${size}】")

                GlideUtil.load(url) {
                    success.invoke(it)
                }
            }
            SOURCE_QQ -> {
                // val url = "https://y.gtimg.cn/music/photo_new/T002R${size}x${size}M000${songData.imageUrl}.jpg?max_age=2592000"
                val url = "https://y.gtimg.cn/music/photo_new/T002R300x300M000${songData.imageUrl}.jpg?max_age=2592000"
                loge("getPlayerActivityCoverBitmapQQ图片url【${url}】")
                GlideUtil.load(url) {
                    success.invoke(it)
                }
            }
            SOURCE_KUWO -> {
                songData.imageUrl?.let { url ->
                    GlideUtil.load(url) {
                        success.invoke(it)
                    }
                }
            }
            SOURCE_LOCAL -> {
                songData.imageUrl?.let {
                    val bitmap = LocalMusic.getBitmapFromUir(context, it.toUri())
                    if (bitmap != null) {
                        success.invoke(bitmap)
                    }
                }
            }
            else -> {
                val commonBitmap: Bitmap? = ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.toBitmap(128.dp(), 128.dp())
                if (commonBitmap != null) {
                    success.invoke(commonBitmap)
                }
            }
        }

    }

}