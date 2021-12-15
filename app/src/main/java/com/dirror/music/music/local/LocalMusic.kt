package com.dirror.music.music.local

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
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.music.standard.data.StandardSongData.LocalInfo
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.plugin.PluginConstants
import com.dirror.music.plugin.PluginSupport
import com.dirror.music.util.Config
import com.dirror.music.util.dp
import java.io.*
import java.lang.Exception

/**
 * 本地音乐
 */
object LocalMusic {

    // 未知错误 query failed, handle error.
    private const val ERROR_UNKNOWN = 0

    // 无音乐 no media on the device
    private const val ERROR_NO_MUSIC = 1

    /**
     * 扫描本地音乐
     * @param success 成功返回歌单集合
     */
    fun scanLocalMusic(context: Context, success: (ArrayList<StandardSongData>) -> Unit, failure: (Int) -> Unit) {
        val songList = ArrayList<StandardSongData>()

        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        // 过滤规则
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val cursor: Cursor? = resolver.query(uri, null, null, null, sortOrder)
        when {
            cursor == null -> {
                failure.invoke(ERROR_UNKNOWN)
            }
            !cursor.moveToFirst() -> {
                failure.invoke(ERROR_NO_MUSIC)
            }
            else -> {
                // 要读取的数据表列
                val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) // 标题，音乐名称
                val songIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID) // 音乐 id
                val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST) // 艺术家
                val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA) // 路径
                val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID) // 专辑 id
                val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE) // 大小
                do {
                    val id = cursor.getLong(songIdColumn)
                    val data = cursor.getString(dataColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val title = cursor.getString(titleColumn)
                    var artist = cursor.getString(artistColumn)
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
                    if (App.mmkv.decodeBool(Config.SMART_FILTER, true)) {
                        if (size <= 500_000) {
                            continue
                        }
                    }
                    // 是否过滤录音
                    if (App.mmkv.decodeBool(Config.FILTER_RECORD, true)) {
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
                    val song = StandardSongData(
                        SOURCE_LOCAL,
                        id.toString(),
                        title,
                        coverUri.toString(),
                        artistList,
                        null,
                        LocalInfo(size, data),
                        null
                    )

                    PluginSupport.setSong(song)
                    val pluginResult = PluginSupport.apply(PluginConstants.POINT_SCAN_LOCAL_MUSIC)
                    if (pluginResult != null && pluginResult is Boolean) {
                        if (pluginResult) {
                            songList.add(song)
                        }
                    } else {
                        songList.add(song)
                    }

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
     * @param albumUri 专辑封面 Uri
     * @return bitmap
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
            bmp = ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.toBitmap(128.dp(), 128.dp())
        }
        return bmp
    }

    /* URI */
    private const val ALBUM_ART_URI = "content://media/external/audio/albumart"

    /**
     * 获取 uri
     * 传入专辑 id [albumId]，返回 Uri
     */
    fun getUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse(ALBUM_ART_URI), albumId)
    }

}