package com.dirror.music.data

data class SkyVersionData(
    val data: ArrayList<DataData>

) {
    data class DataData(
        val name: String,
        val code: Int
    )
}
