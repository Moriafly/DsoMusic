package com.dirror.music.music.standard.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * 标准歌单数据类
 * @author Moriafly
 */
@Keep
@Parcelize
data class StandardPlaylistData(
    // 歌单名字
    val name: String,
    // 歌单描述
    val description: String,
    // 歌单图片
    val picUrl: String,
    // 内含歌曲
    val songs: ArrayList<StandardSongData>
): Parcelable