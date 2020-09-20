package com.dirror.music.music

import android.util.Log
import com.dirror.music.CloudMusic
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.cloudmusic.TrackIdsData
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.toast
import com.google.gson.Gson
import kotlin.concurrent.thread

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


    private var trackIds: List<TrackIdsData>? = null
    var i = 0
    var standardSongDataList = ArrayList<StandardSongData>()
    private fun detailPlaylistDataToStandardSongDataList(
        detailPlaylistData: DetailPlaylistData,
        success: (ArrayList<StandardSongData>) -> Unit
    ) {
        // 获取全 id
        trackIds = detailPlaylistData.playlist.trackIds
        // 初始化歌单
        // val sortStandardSongDataList = ArrayList<SortStandardSongData>()
        standardSongDataList.clear()
        i = 0
        read(){
            success.invoke(standardSongDataList)
        }

//        var count = -1
//        var songSum = trackIds!!.lastIndex
//
//        var haveReadThisSong = false
//        for (trackId in 0..trackIds!!.lastIndex) {  // 遍历 id
//            haveReadThisSong = false
//            // Log.e("歌曲id", trackIds[trackId].id.toString())
//            CloudMusic.getSongDetail(trackIds!![trackId].id, { data ->
//                // lastIndex = sortStandardSongDataList.lastIndex
//                count = count.plus(1)
//                sortStandardSongDataList.add(SortStandardSongData(trackId, data))
//
//
//
//                // standardSongDataList.add(data)
//                loge("比较，计数${count}总数${songSum}")
//                if (sortStandardSongDataList.lastIndex == songSum || count == songSum) {
//                    // 全部加载完全
//                    sortStandardSongDataList.sortBy {
//                        it.index
//                    }
//
//                    for (index in 0..sortStandardSongDataList.lastIndex) {
//                        loge("歌单 - 排序成功：${index} - ${sortStandardSongDataList[index].song.name}")
//                        standardSongDataList.add(sortStandardSongDataList[index].song)
//                    }
//
//                    success.invoke(standardSongDataList)
//                } else {
//                    // loge(sortStandardSongDataList.lastIndex.toString())
//                }
//                standardSongDataList.add(data)
//                loge("获取成功${trackId}")
//                haveReadThisSong = true
//
//            }, {
//                toast(it)
//                songSum = songSum.minus(1)
//            })
//
//            while (true) {
//                // Thread.sleep(1)
//                // loge(haveReadThisSong.toString())
//                if (haveReadThisSong) {
//                    break
//                }
//            }
//        }
//
//
//        success.invoke(standardSongDataList)


    }

    private fun read(success: (Boolean) -> Unit) {
        trackIds?.get(i)?.id?.let {
            CloudMusic.getSongDetail(it, { data ->
                standardSongDataList.add(data)
                // toast("读取 ${i + 1} / ${trackIds!!.lastIndex + 1}")
                if (i == trackIds!!.lastIndex) {
                    success.invoke(true)
                } else {
                    i++
                    read {
                        success.invoke(true)
                    }
                }
            }, {

            })
        }
    }

    data class SortStandardSongData(
        val index: Int,
        val song: StandardSongData
    )

}





