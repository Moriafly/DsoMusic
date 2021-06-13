package com.dirror.music.music.netease.data

import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData

data class PlaylistDetail(
    var playlist: Playlist?,
    var privileges: ArrayList<Privilege>?
) {
    data class Playlist(
        var name: String?,
        var coverImgUrl: String?,
        var creator: Creator?,
        var tracks: ArrayList<Track>?
    ) {
        data class Creator(
            var nickname: String?,
            var avatarUrl: String?
        )

        data class Track(
            var name: String?,
            var id: Long?,
            var ar: ArrayList<Artist>,
            var al: Album?,

        ) {

            data class Artist(
                var id: Long?,
                var name: String?
            ) {
                fun toCompat(): StandardSongData.StandardArtistData {
                    return StandardSongData.StandardArtistData(
                        this.id,
                        this.name
                    )
                }
            }

            /** 专辑 */
            data class Album(
                var id: Long?,
                var name: String?,
                var picUrl: String?
            )

        }
    }

    data class Privilege(
        var fee: Int?,
        var id: Long?,
        var pl: Int?,
        var maxbr: Int?,
        var flag: Int?
    )

    fun getSongArrayList(): ArrayList<StandardSongData> {
        val standardPlaylistData = ArrayList<StandardSongData>()
        // 防止遍历空集合
        if (this.playlist?.tracks?.isNotEmpty() == true) {
            for ((index, song) in this.playlist?.tracks!!.withIndex()) {
                val standardArtistDataList = ArrayList<StandardSongData.StandardArtistData>()
                // song.artists
                for (i in 0..song.ar.lastIndex) {
                    val standardArtistData = StandardSongData.StandardArtistData(
                        song.ar[i].id,
                        song.ar[i].name
                    )
                    standardArtistDataList.add(standardArtistData)
                }

                val privileges = this.privileges?.get(index)

                val standardSongData = StandardSongData(
                    SOURCE_NETEASE,
                    song.id.toString(),
                    song.name,
                    song.al?.picUrl,
                    standardArtistDataList,
                    StandardSongData.NeteaseInfo(
                        privileges?.fee ?: 0,
                        privileges?.pl,
                        privileges?.flag,
                        privileges?.maxbr
                    ),
                    null,
                    null
                )
                standardPlaylistData.add(standardSongData)
            }
        }
        return standardPlaylistData
    }

}
