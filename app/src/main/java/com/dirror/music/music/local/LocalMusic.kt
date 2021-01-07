package com.dirror.music.music.local

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.Keep
import com.dirror.music.MyApplication
import com.dirror.music.music.standard.data.StandardSongData.LocalInfo
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.StandardSongData.StandardArtistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Config
import com.dirror.music.util.toast
import java.io.*
import java.net.URL


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
                // val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.VOLUME_NAME)
                do {
                    val id = cursor.getLong(idColumn) // 音乐 id
                    val title = cursor.getString(titleColumn) // 音乐名称
                    val artist = cursor.getString(artistColumn) // 艺术家
                    // val bitrate = cursor.getString(bitrateColumn)
                    val size = cursor.getLong(sizeColumn)
                    // 过滤无法播放的歌曲
                    if (title == "" && artist == "<unknown>") {
                        continue
                    }
                    // 大小为 0 过滤
                    if (size == 0L) {
                        continue
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
                            "",
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

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     *
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     *
     * B.本地路径: url="file://mnt/sdcard/photo/image.png";
     *
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return
     */
    @JvmStatic
    fun getLocalOrNetBitmap(url: String?): Bitmap? {
        val inputStream: InputStream?
        val bufferedOutputStream: BufferedOutputStream?

        return try {
            inputStream = BufferedInputStream(URL(url).openStream(), 1024)

            val dataStream = ByteArrayOutputStream()
            bufferedOutputStream = BufferedOutputStream(dataStream, 1024)

            copy(inputStream, bufferedOutputStream)
            bufferedOutputStream.flush()

            val data = dataStream.toByteArray()
            BitmapFactory.decodeByteArray(data, 0, data.size)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    @Throws(IOException::class)
    private fun copy(inputStream: InputStream, out: OutputStream) {
        val bytes = ByteArray(1024)
        var read: Int
        while (inputStream.read(bytes).also { read = it } != -1) {
            out.write(bytes, 0, read)
        }
    }

}