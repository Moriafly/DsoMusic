package com.dirror.music.music.local

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import com.dirror.music.music.standard.data.LocalInfo
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData
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
                // val bitrateColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.BITRATE) // 码率
                val sizeColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.SIZE) // 码率
                do {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    // val bitrate = cursor.getString(bitrateColumn)
                    val size = cursor.getLong(sizeColumn)
                    // 过滤无法播放的歌曲
                    if (title == "" && artist == "<unknown>") {
                        continue
                    }
                    // loge("本地歌曲：$id，标题【$title】，艺术家【$artist】")

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
                            null,
                            LocalInfo(size)
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