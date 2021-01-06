package com.dirror.music.music.netease.data

import androidx.annotation.Keep

@Keep
data class SearchHotData(
    val code: Int,
    val data: ArrayList<DataData>
) {
    data class DataData(
        val searchWord: String,
        val content: String,
        val score: Int,
        val iconType: Int
    )
}
