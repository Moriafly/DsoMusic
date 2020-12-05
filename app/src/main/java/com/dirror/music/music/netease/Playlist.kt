package com.dirror.music.music.netease

import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.toast
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly

/**
 * 获取网易云歌单全部，对于大型歌单也要成功
 */
object Playlist {

    private const val SPLIT_PLAYLIST_NUMBER = 500 // 切割歌单，每 200 首
    private const val CHEATING_CODE = -460 // Cheating 错误

    private const val PLAYLIST_URL = "${API_MUSIC_ELEUU}/playlist/detail?id=" // 获取歌单链接

    // private const val SONG_DETAIL_URL = "https://music.163.com/api/song/detail" // 歌曲详情
    // private const val SONG_DETAIL_URL = "${API_MUSIC_ELEUU}/song/detail" // 歌曲详情
    private const val SONG_DETAIL_URL = "https://autumnfish.cn/song/detail" // 歌曲详情


    /**
     * 传入歌单 [playlistId] id
     */
    @TestOnly
    fun getPlaylist(playlistId: Long, success: (ArrayList<StandardSongData>) -> Unit, failure: () -> Unit) {
        // 请求链接
        val url = PLAYLIST_URL + playlistId
        // 发送 Get 请求获取全部 trackId
        MagicHttp.OkHttpManager().newGet(url, { response ->
            // 解析得到全部 trackIds
            val trackIds = ArrayList<Long>()
            Gson().fromJson(response, PlaylistData::class.java).playlist?.trackIds?.forEach {
                trackIds.add(it.id)
            }
            // POST 请求全部 trackIds
            // 每 200 首请求一次
            val allSongData = ArrayList<StandardSongData>()
            averageAssignFixLength(trackIds, SPLIT_PLAYLIST_NUMBER).forEach { list ->
                var json = Gson().toJson(list)

                json = json.replace("[", "")
                json = json.replace("]", "")

                loge("json:${json}")




                MagicHttp.OkHttpManager().post(SONG_DETAIL_URL, json) { response ->
                    // toast("服务器返回字符数：${response.length.toString()}")
                    val data = Gson().fromJson(response, CompatSearchData::class.java)
                    if (data.code == CHEATING_CODE) {
                        toast("-460 Cheating")
                        // 发生了欺骗立刻返回
                        success.invoke(allSongData)
                    } else {
                        compatSearchDataToStandardPlaylistData(data).forEach {
                            allSongData.add(it)
                        }
                        // 全部读取完成再返回
                        if (allSongData.size == trackIds.size) {
                            success.invoke(allSongData)
                        }
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
    )

    data class TrackIds(
        val trackIds: ArrayList<TrackId>?
    )

    data class TrackId(
        val id: Long
    )

    private fun <T> averageAssignFixLength(source: List<T>?, splitItemNum: Int): List<List<T>> {
        val result = ArrayList<List<T>>()

        if (source != null && source.run { isNotEmpty() } && splitItemNum > 0) {
            if (source.size <= splitItemNum) {
                // 源List元素数量小于等于目标分组数量
                result.add(source)
            } else {
                // 计算拆分后list数量
                val splitNum = if (source.size % splitItemNum == 0) source.size / splitItemNum else source.size / splitItemNum + 1

                var value: List<T>? = null
                for (i in 0 until splitNum) {
                    if (i < splitNum - 1) {
                        value = source.subList(i * splitItemNum, (i + 1) * splitItemNum)
                    } else {
                        // 最后一组
                        value = source.subList(i * splitItemNum, source.size)
                    }

                    result.add(value)
                }
            }
        }

        return result
    }


}