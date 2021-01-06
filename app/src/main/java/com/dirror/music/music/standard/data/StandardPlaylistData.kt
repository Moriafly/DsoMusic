package com.dirror.music.music.standard.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * 标准歌单数据类
 */
@Keep
@Parcelize
data class StandardPlaylistData(
    val name: String, // 歌单名字
    val description: String, // 描述
    val picUrl: String,
    val songs: ArrayList<StandardSongData> // 内含歌曲
): Parcelable