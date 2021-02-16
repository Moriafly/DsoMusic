package com.dirror.music.music.netease.data

import com.dirror.music.music.standard.data.SOURCE_NETEASE
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
            val reason: String,

            val privilege: PrivilegeData,
        ) {
            data class AlbumData(
                val id: String,
                val name: String,
                val picUrl: String
            )
            data class PrivilegeData(
                val fee: Int,
                val pl: Int?,
                val flag: Int?,
                val maxbr: Int?,
            )
        }
    }
}

fun ArrayList<DailyRecommendSongData.DataData.DailySongsData>.toStandardSongDataArrayList(): ArrayList<StandardSongData> {
    val standardSongDataArrayList = ArrayList<StandardSongData>()
    this.forEach {
        val songData = StandardSongData(
            SOURCE_NETEASE,
            it.id,
            it.name,
            it.al.picUrl,
            it.ar,
            StandardSongData.NeteaseInfo(
                it.privilege.fee,
                it.privilege.pl,
                it.privilege.flag,
                it.privilege.maxbr),
            null,
            null
        )
        standardSongDataArrayList.add(songData)
    }
    return standardSongDataArrayList
}
