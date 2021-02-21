package com.dirror.music.music.netease.data

/**
 * 标准网易云歌曲数据类
 */
data class NeteaseSongData(
    val name: String,
    val id: Long,
    val artists: ArrayList<NeteaseArtistData>,
    val album: NeteaseAlbumData,
) {
    /**
     * 标准网易云艺术家类
     */
    data class NeteaseArtistData(
        val name: String,
        val id: Long,
        val picUrl: String,
    )

    /**
     * 标准网易云专辑类
     */
    data class NeteaseAlbumData(
        val name: String,
        val id: Long
    )
}
