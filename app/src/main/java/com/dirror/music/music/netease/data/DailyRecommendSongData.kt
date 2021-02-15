package com.dirror.music.music.netease.data

import com.dirror.music.music.standard.data.StandardSongData

data class DailyRecommendSongData(
    val code: Int,
    val data: DataData,
) {
    data class DataData(
        val dailySongs: ArrayList<DailySongsData>,
    ) {
        data class DailySongsData(
            val name: String,
            val id: String,
            val ar: ArrayList<StandardSongData.StandardArtistData>,
            val al: AlbumData,
            val reason: String
        ) {
            data class AlbumData(
                val id: String,
                val name: String,
                val picUrl: String
            )
        }
    }
}
