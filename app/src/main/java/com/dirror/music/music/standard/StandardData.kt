package com.dirror.music.music.standard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val SOURCE_LOCAL = 0
const val SOURCE_NETEASE = 1
const val SOURCE_QQ = 2

val EMPTY_STANDARD_SONG = StandardSongData(
    SOURCE_LOCAL,
    "-1",
    "",
    "",
    ArrayList()
)

@Parcelize
data class StandardSongData(
    val source: Int, // 歌曲来源，网易，QQ，本地
    val id: String?,
    val name: String,
    val imageUrl: String?,
    val artists: ArrayList<StandardArtistData>?, //
): Parcelable

@Parcelize
data class StandardArtistData(
    val id: Long?,
    val name: String?
): Parcelable


@Parcelize
data class StandardLocalPlaylistData(
    val playlists: ArrayList<StandardPlaylistData>
): Parcelable

@Parcelize
data class StandardPlaylistData(
    val name: String,
    val songs: ArrayList<StandardSongData>
): Parcelable