package com.dirror.music.music.netease.data

import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_NETEASE_CLOUD
import com.dirror.music.music.standard.data.StandardSongData

/**
 * 用户云盘数据
 */
data class UserCloudData(
    val data: ArrayList<SongData>,
    val count: Int, // 多少首
    val size: String, // 已用空间
    val maxSize: String, // 所有空间
    val upgradeSign: Int,
    val hasMore: Boolean, // 是否有更多
    val code: Int
    ) {
    data class SongData(
        val fileSize: Long,
        val album: String,
        val artist: String,
        val bitrate: Int,
        val songId: Long,
        val addTime: Long,
        val songName: String,
        val cover: Long,
        val coverId: String,
        val lyricId: String,
        val version: Int,
        val fileName: String,
        val simpleSong: SimpleSong
//        val name: String, // 歌曲名
//        val id: Long, // 歌曲 id
//        val ar: ArrayList<StandardSongData.StandardArtistData>,
//        val al: NeteaseAlbumData,
    ) {
        data class SimpleSong(
            val al: Album
        ) {
            data class Album(
                var picUrl: String
            )
        }
    }
}

fun ArrayList<UserCloudData.SongData>.toStandard(): ArrayList<StandardSongData> {
    val list = ArrayList<StandardSongData>()
    this.forEach {
        list.add(it.toStandard())
    }
    return list
}

/**
 * 转标准
 */
fun UserCloudData.SongData.toStandard(): StandardSongData {
    return StandardSongData(
        SOURCE_NETEASE_CLOUD,
        songId.toString(),
        name = songName,
        simpleSong.al.picUrl,
        arrayListOf(StandardSongData.StandardArtistData(
            null, this.artist
        )),
        null,
        null,
        null
    )
}
