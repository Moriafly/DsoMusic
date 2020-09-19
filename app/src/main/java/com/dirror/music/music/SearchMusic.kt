package com.dirror.music.music

import android.os.Parcelable
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.util.MagicHttp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

object SearchMusic {
    fun searchMusic(keywords: String) {
        val url = "${API_MUSIC_API}/search?keywords=${keywords}"
        MagicHttp.OkHttpManager().newGet(url, {

        }, {

        })
    }
}

/**
 * 兼容性歌曲信息单数据
 * 支持网易云
 *
 *
 */
//@Parcelize
//data class CompatSongData(
//    @SerializedName("id") val id: Long, // 歌曲 id
//    @SerializedName("name") val name: String, // 歌曲名称
//
//): Parcelable

