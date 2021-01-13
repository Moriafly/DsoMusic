package com.dirror.music.music.local.data

import android.os.Parcelable
import com.dirror.music.music.standard.data.StandardPlaylistData
import kotlinx.parcelize.Parcelize

/**
 * 本地歌单集合类
 */
@Parcelize
data class LocalPlaylistArrayData(
    // 数据
    val data: ArrayList<StandardPlaylistData>
): Parcelable
