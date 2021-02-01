package com.dirror.music.music.local

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.music.standard.data.StandardSongData.LocalInfo
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.util.Config
import com.dirror.music.util.toast
import java.io.*
import java.lang.Exception

/**
 * 本地音乐
 */
object LocalMusic {

    fun scanLocalMusic(activity: Activity, success: (ArrayList<StandardSongData>) -> Unit, failure: () -> Unit) {


        val songList = ArrayList<StandardSongData>()

        val resolver: ContentResolver = activity.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        // val songSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
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
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

                val dataColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                // val albumArtColumn = cursor.getColumnIndex("album_art")
                // val bitrateColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.BITRATE) // 码率
                val sizeColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE) // 大小
                // val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.VOLUME_NAME)
                do {
                    val id = cursor.getLong(idColumn) // 音乐 id
                    val dataPath = cursor.getString(dataColumn)
                    val albumId = cursor.getLong(albumIdColumn) // 专辑 id
                    val title = cursor.getString(titleColumn) // 音乐名称
                    var artist = cursor.getString(artistColumn) // 艺术家
                    // val bitrate = cursor.getLong(bitrateColumn)
                    val size = cursor.getLong(sizeColumn)


                    val coverUri = getAlbumCover(albumId)
                    // 过滤无法播放的歌曲
                    if (title == "" && artist == "<unknown>") {
                        continue
                    }
                    if (artist == "<unknown>") {
                        artist = "未知歌手"
                    }
                    // 大小为 0 过滤
                    if (size == 0L) {
                        continue
                    }
                    if (MyApplication.mmkv.decodeBool(Config.SMART_FILTER, true)) {
                        if (size <= 500_000) {
                            continue
                        }
                    }
                    // 是否过滤录音
                    if (MyApplication.mmkv.decodeBool(Config.FILTER_RECORD, true)) {
                        if (artist == "Meizu Recorder") {
                            continue
                        }
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
                            coverUri.toString(),
                            artistList,
                            null,
                            LocalInfo(size),
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


    private fun getAlbumCover(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    /**
     * 从 Uri 获取音乐
     * @param albumUri 专辑封面
     */
    fun getBitmapFromUir(context: Context, albumUri: Uri): Bitmap? {
        val `in`: InputStream?
        var bmp: Bitmap? = null
        try {
            `in` = context.contentResolver.openInputStream(albumUri)
            val sBitmapOptions = BitmapFactory.Options()
            bmp = BitmapFactory.decodeStream(`in`, null, sBitmapOptions)
            `in`?.close()
        } catch (e: Exception) {

        }
        if (bmp == null) {
            bmp = ContextCompat.getDrawable(context, R.drawable.bq_no_data_song)?.toBitmap()
        }
        return bmp
    }

}