package com.dirror.music.music.standard.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 标准歌曲信息
 */
@Parcelize
data class StandardSongData(
    val source: Int, // 歌曲来源，网易，QQ，本地
    val id: String, // id
    val name: String, // 歌曲名称
    val imageUrl: String?, // 图片 url
    val artists: ArrayList<StandardArtistData>?, // 艺术家
    val neteaseInfo: NeteaseInfo?,
    val localInfo: LocalInfo?
) : Parcelable {

    @Parcelize
    data class StandardArtistData(
        val id: Long?,
        val name: String?
    ) : Parcelable

    /**
     * 网易云音乐拓展信息
     */
    @Parcelize
    data class NeteaseInfo(
        val fee: Int, // 是否是网易云 vip 歌曲，0 代表没版权，1 代表 vip
    ) : Parcelable

    /**
     * 本地歌曲拓展信息
     */
    @Parcelize
    data class LocalInfo(
        val size: Long,
    ) : Parcelable

}
