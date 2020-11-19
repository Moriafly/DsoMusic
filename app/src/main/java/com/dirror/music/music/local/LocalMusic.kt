package com.dirror.music.music.local

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import com.dirror.music.music.standard.SOURCE_LOCAL
import com.dirror.music.music.standard.StandardArtistData
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.util.toast

object LocalMusic {

    fun scanLocalMusic(activity: Activity, success: (ArrayList<StandardSongData>) -> Unit, failure: () -> Unit) {

        val songList = ArrayList<StandardSongData>()

        val resolver: ContentResolver = activity.contentResolver
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        when {
            cursor == null -> {
                // query failed, handle error.
                toast("错误")
                failure.invoke()
            }
            !cursor.moveToFirst() -> {
                // no media on the device
                toast("无音乐")
                failure.invoke()
            }
            else -> {
                val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                val artistColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
                do {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)

                    val artistList = ArrayList<StandardArtistData>()
                    artistList.add(
                        StandardArtistData(
                            null,
                            artist
                        )
                    )
                    songList.add(
                        StandardSongData(
                            SOURCE_LOCAL,
                            id.toString(),
                            title,
                            "",
                            artistList,
                            null
                        )
                    )

                    // ...process entry...
                } while (cursor.moveToNext())
                success.invoke(songList)
            }
        }
        cursor?.close()


    }

}