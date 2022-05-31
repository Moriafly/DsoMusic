package com.dirror.music.music.netease

import android.content.Context
import android.util.Log
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.manager.User
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.netease.data.PlaylistDetail
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.playlist.SongPlaylistViewModel
import com.dirror.music.util.*
import com.dso.ext.averageAssignFixLength
import com.google.gson.Gson
import okhttp3.*
import org.jetbrains.annotations.TestOnly
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * 获取网易云歌单全部，对于大型歌单也要成功
 */
object Playlist {

    private const val SPLIT_PLAYLIST_NUMBER = 1000 // 切割歌单
    private const val CHEATING_CODE = -460 // Cheating 错误

    private const val PLAYLIST_URL = "${API_MUSIC_ELEUU}/playlist/detail?id=" // 获取歌单链接

    // private const val SONG_DETAIL_URL = "https://music.163.com/api/song/detail" // 歌曲详情
    // private const val SONG_DETAIL_URL = "${API_AUTU}/song/detail" // 歌曲详情
    // private const val SONG_DETAIL_URL = "https://autumnfish.cn/song/detail" // 歌曲详情
    // private const val SONG_DETAIL_URL = "http://www.hjmin.com/song/detail"
    private const val SONG_DETAIL_URL = "http://music.eleuu.com/song/detail"
    // private const val SONG_DETAIL_URL = "${API_DSO}/song/detail"
    private const val API = "https://music.163.com/api/v6/playlist/detail"

    private const val SONG_DETAIL_API = "https://music.163.com/api/v3/song/detail"

    /**
     * 传入歌单 [playlistId] id
     */
    @TestOnly
    fun getPlaylist(
        context: Context,
        playlistId: Long,
        success: (ArrayList<StandardSongData>) -> Unit,
        failure: () -> Unit
    ) {
        getPlaylistUid(context, playlistId, success, failure)
    }

    private fun getPlaylistUid(
        context: Context,
        playlistId: Long,
        success: (ArrayList<StandardSongData>) -> Unit,
        failure: () -> Unit
    ) {
        val url = PLAYLIST_URL + playlistId + "&cookie=${AppConfig.cookie}"
        MagicHttp.OkHttpManager().getByCache(context, url, { response ->
            val trackIds = ArrayList<Long>()
            try {
                Gson().fromJson(response, PlaylistData::class.java).playlist?.trackIds?.forEach {
                    trackIds.add(it.id)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
            // POST 请求全部 trackIds
            val allSongData = ArrayList<StandardSongData>()

            trackIds.averageAssignFixLength(SPLIT_PLAYLIST_NUMBER).forEach { list ->
                var json = Gson().toJson(list)
                json = json.replace("[", "")
                json = json.replace("]", "")

                loge(json, "json")
                val cList = ArrayList<String>()
                list.forEach {
                    val a = "{\"id\":${it}}"
                    loge(a, "json")
                    cList.add(a)
                }
                var c3 = "["
                cList.forEach {
                    c3 = "$c3$it,"
                }
                c3 = c3.substring(0, c3.lastIndex)
                c3 = "$c3]"
                loge(c3, "json")
                val requestBody = FormBody.Builder()
//                    .add("c", c3)
                    .add("ids", json)
                    .add("crypto", "weapi")
                    // .add("cookie", MyApplication.userManager.getCloudMusicCookie())
                    .add("withCredentials", "true")
                    .add("realIP", "211.161.244.70")
                    .build()
                MagicHttp.OkHttpManager().newPost(SONG_DETAIL_URL, requestBody, {
                    loge(it, "json")
                    try {
                        val data = Gson().fromJson(it, CompatSearchData::class.java)
                        if (data.code == CHEATING_CODE) {
                            toast("-460 Cheating")
                            // 发生了欺骗立刻返回
                            success.invoke(allSongData)
                        } else {
                            compatSearchDataToStandardPlaylistData(data).forEach { songData ->
                                allSongData.add(songData)
                            }
                            // 全部读取完成再返回
                            if (allSongData.size == trackIds.size) {
                                success.invoke(allSongData)
                            }
                        }
                    } catch (e: Exception) {
                        failure.invoke()
                    }
                }, {

                })
            }

        }, {
            // 获取 trackId 失败
            failure.invoke()
        })
    }

    /**
     * 网易云登录成功的用户通过这个接口获取歌单歌曲
     */
    fun getPlaylistByCookie(playlistId: Long, success: (ArrayList<StandardSongData>) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("id", playlistId.toString())
            .add("cookie", AppConfig.cookie)
            .build()

        val api = "${User.cookie}/playlist/detail"
        try {
            val client = OkHttpClient.Builder()
                // .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("110.243.17.83", 9999)))
                // .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build()

            val request: Request = Request.Builder()
                .url("${api}?timestamp=${getCurrentTime()}")
//                    .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
//                    .addHeader("Connection", "close")
//                    .addHeader("Content-Length", "80")
//                    .addHeader("X-Real-IP", "211.161.244.70")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.body()?.byteStream()?.let { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        var playlistDetail: PlaylistDetail? = null
                        try {
                            playlistDetail = Gson().fromJson(reader, PlaylistDetail::class.java)
                            Log.i(SongPlaylistViewModel.TAG, "Gson 解析完成 ${System.currentTimeMillis()}")
                        } catch (e: Exception) {

                        }
                        playlistDetail?.getSongArrayList()?.let { success(it) }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // failure(ErrorCode.ERROR_MAGIC_HTTP)
                }
            })
        } catch (e: Exception) {
            // failure(ErrorCode.ERROR_MAGIC_HTTP)
        }


//        MagicHttp.OkHttpManager().newPost("${User.cookie}/playlist/detail", requestBody, { response ->
//            Log.i(SongPlaylistViewModel.TAG, "获取到服务器返回 ${System.currentTimeMillis()}")
//
//        }, {
//            // failure(ErrorCode.MAGIC_HTTP)
//        })
    }

    data class PlaylistData(
        val playlist: TrackIds?
    ) {
        data class TrackIds(
            val trackIds: ArrayList<TrackId>?
        ) {
            data class TrackId(
                val id: Long
            )
        }
    }
}