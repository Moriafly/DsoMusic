package com.dirror.music.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dirror.music.music.standard.data.StandardSongData

@Entity
@TypeConverters(StandardArtistDataConverter::class)
data class MyFavoriteData(
    @Embedded
    var songData: StandardSongData) {

    @PrimaryKey(autoGenerate = true)
    var databaseId: Long = 0

}
