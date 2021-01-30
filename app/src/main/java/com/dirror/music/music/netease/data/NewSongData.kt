package com.dirror.music.music.netease.data


data class NewSongData(
    val code: Int,
    val result: ArrayList<ResultData>
) {
    data class ResultData(
        val id: Long,
        val name: String,
        val picUrl: String,
        val song: SongData
    ) {
        data class SongData(
            val fee: Int,
            val artists: ArrayList<NewSongArtistsData>,
            val album: AlbumData,
            val privilege: PrivilegeData
        ) {
            data class NewSongArtistsData(
                val name: String,
                val id: Long
            )
            data class AlbumData(
                val name: String,
                val id: Long
            )
            data class PrivilegeData(
                val pl: Int,
                val maxbr: Int,
                val flag: Int
            )
        }
    }
}
