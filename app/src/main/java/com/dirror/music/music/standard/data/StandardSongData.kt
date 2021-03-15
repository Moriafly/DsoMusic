package com.dirror.music.music.standard.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 标准歌曲信息
 */
@Keep
@Parcelize
data class StandardSongData(
    val source: Int?, // 歌曲来源，网易，QQ，本地
    val id: String?, // 歌曲 id

    val name: String?, // 歌曲名称
    val imageUrl: String?, // 图片 url
    val artists: ArrayList<StandardArtistData>?, // 艺术家

    @Embedded
    val neteaseInfo: NeteaseInfo?,

    @Embedded
    val localInfo: LocalInfo?,

    @Embedded
    val dirrorInfo: DirrorInfo?
) : Parcelable {

    constructor() : this(SOURCE_NETEASE,
    null, null, null, null, null, null, null)

    /**
     * 标准艺术家数据
     */
    @Parcelize
    data class StandardArtistData(
        @SerializedName("id") val artistId: Long?, // 艺术家 id
        val name: String? // 艺术家名称
    ) : Parcelable

    /**
     * 网易云音乐拓展信息
     */
    @Parcelize
    data class NeteaseInfo(
        val fee: Int, // 是否是网易云 vip 歌曲，1 代表 vip

        val pl: Int?, // 0 为无效歌曲，就是网易云没版权
        val flag: Int?, //
        val maxbr: Int? // 最大音质
    ) : Parcelable

    /**
     * 本地歌曲拓展信息
     */
    @Parcelize
    data class LocalInfo(
        val size: Long,
    ) : Parcelable

    /**
     * Dirror 源拓展信息
     */
    @Parcelize
    data class DirrorInfo(
        val url: String?
    ) : Parcelable

}
