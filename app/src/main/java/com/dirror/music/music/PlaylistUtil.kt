package com.dirror.music.music

import android.util.Log
import com.dirror.music.CloudMusic
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.toast
import com.google.gson.Gson

object PlaylistUtil {

    fun getDetailPlaylist(id: Long, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        loge("准备获取详细歌单${id}")
        val url = "$API_MUSIC_API/playlist/detail?id=$id"
        MagicHttp.OkHttpManager().newGet(url, { response ->

            val detailPlaylistData = Gson().fromJson(response, DetailPlaylistData::class.java)
            loge("读取歌单 json 成功")
            detailPlaylistDataToStandardSongDataList(detailPlaylistData) {
                loge("转换成功")
                success.invoke(it)
            }
        }, {
            failure.invoke(it)
        })
    }

    private fun detailPlaylistDataToStandardSongDataList(
        detailPlaylistData: DetailPlaylistData,
        success: (ArrayList<StandardSongData>) -> Unit
    ) {


        // 获取全 id

        val trackIds = detailPlaylistData.playlist.trackIds

        // 初始化歌单
        val sortStandardSongDataList = ArrayList<SortStandardSongData>()

        val standardSongDataList = ArrayList<StandardSongData>()


        var count = -1
        var songSum = trackIds.lastIndex
        for (trackId in 0..trackIds.lastIndex) {  // 遍历 id

            // Log.e("歌曲id", trackIds[trackId].id.toString())
            CloudMusic.getSongDetail(trackIds[trackId].id, { data ->
                // lastIndex = sortStandardSongDataList.lastIndex
                count = count.plus(1)
                sortStandardSongDataList.add(SortStandardSongData(trackId, data))


                // standardSongDataList.add(data)
                loge("比较，计数${count}总数${songSum}")
                if (sortStandardSongDataList.lastIndex == songSum) {
                    // 全部加载完全
                    sortStandardSongDataList.sortBy {
                        it.index
                    }

                    for (index in 0..sortStandardSongDataList.lastIndex) {
                        loge("歌单 - 排序成功：${index} - ${sortStandardSongDataList[index].song.name}")
                        standardSongDataList.add(sortStandardSongDataList[index].song)
                    }

                    success.invoke(standardSongDataList)
                } else {
                    // loge(sortStandardSongDataList.lastIndex.toString())
                }
            }, {
                toast(it)
                songSum = songSum.minus(1)
            })
        }


    }

    data class SortStandardSongData(
        val index: Int,
        val song: StandardSongData
    )

}





