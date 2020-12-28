package com.dirror.music.music.standard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val SOURCE_LOCAL = 1
const val SOURCE_NETEASE = 2
const val SOURCE_QQ = 3
const val SOURCE_DIRROR = 4

val EMPTY_STANDARD_SONG = StandardSongData(
    SOURCE_LOCAL,
    "-1",
    "",
    "",
    ArrayList(),
    null,
    null,
    null
)

@Parcelize
data class StandardLocalPlaylistData(
    val playlists: ArrayList<StandardPlaylistData>
) : Parcelable



