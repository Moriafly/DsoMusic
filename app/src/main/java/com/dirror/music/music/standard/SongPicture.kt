package com.dirror.music.music.standard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.dp
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
    fun getSongPicture(songData: StandardSongData, type: Int, success: (Bitmap) -> Unit) {
        // 普通背景
        if (songData.source == SOURCE_LOCAL) {
            val commonBitmap: Bitmap? = MyApplication.context.getDrawable(R.drawable.bq_no_data_song)?.toBitmap()
            if (commonBitmap != null) {
                success.invoke(commonBitmap)
            }
        } else {
            val url = getSongPictureUrl(songData, type)
            GlideUtil.load(url) {
                success.invoke(it)
            }
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
                        "$API_FCZBL_VIP/?type=cover&id=${songData.id}"
                    }
                }

            }
            SOURCE_QQ -> {
                "https://y.gtimg.cn/music/photo_new/T002R300x300M000${songData.imageUrl as String}.jpg?max_age=2592000"
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
    fun getPlayerActivityCoverBitmap(songData: StandardSongData, size: Int, success: (Bitmap) -> Unit) {
        when(songData.source) {
            SOURCE_NETEASE -> {
                loge("getPlayerActivityCoverBitmap网易云图片原url【${songData.imageUrl}】")
                val url = if (songData.imageUrl == "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                    || songData.imageUrl == "https://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg") {
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
            else -> {
                val commonBitmap: Bitmap? = MyApplication.context.getDrawable(R.drawable.bq_no_data_song)?.toBitmap()
                if (commonBitmap != null) {
                    success.invoke(commonBitmap)
                }
            }
        }

    }

}