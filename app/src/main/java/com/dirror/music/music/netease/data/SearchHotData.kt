package com.dirror.music.music.netease.data

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
