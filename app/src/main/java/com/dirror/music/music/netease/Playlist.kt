package com.dirror.music.music.netease

import android.content.Context
import com.dirror.music.MyApplication
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.getCurrentTime
import com.dirror.music.util.loge
import com.dirror.music.util.toast
import com.google.gson.Gson
import okhttp3.FormBody
import org.jetbrains.annotations.TestOnly

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
        val url = PLAYLIST_URL + playlistId
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

            averageAssignFixLength(trackIds, SPLIT_PLAYLIST_NUMBER).forEach { list ->
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
                MagicHttp.OkHttpManager().newPost("$SONG_DETAIL_URL?timestamp=${getCurrentTime()}", requestBody) {
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
                }
            }

        }, {
            // 获取 trackId 失败
            failure.invoke()
        })
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

    /**
     * 切割歌单
     */
    private fun <T> averageAssignFixLength(source: List<T>?, splitItemNum: Int): List<List<T>> {
        val result = ArrayList<List<T>>()
        if (source != null && source.run { isNotEmpty() } && splitItemNum > 0) {
            if (source.size <= splitItemNum) {
                // 源List元素数量小于等于目标分组数量
                result.add(source)
            } else {
                // 计算拆分后list数量
                val splitNum =
                    if (source.size % splitItemNum == 0) source.size / splitItemNum else source.size / splitItemNum + 1

                var value: List<T>? = null
                for (i in 0 until splitNum) {
                    value = if (i < splitNum - 1) {
                        source.subList(i * splitItemNum, (i + 1) * splitItemNum)
                    } else {
                        // 最后一组
                        source.subList(i * splitItemNum, source.size)
                    }
                    result.add(value)
                }
            }
        }
        return result
    }

}