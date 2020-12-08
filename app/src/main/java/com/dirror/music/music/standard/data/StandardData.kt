package com.dirror.music.music.standard.data

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
    ArrayList(),
    null,
    null
)

@Parcelize
data class StandardLocalPlaylistData(
    val playlists: ArrayList<StandardPlaylistData>
) : Parcelable



