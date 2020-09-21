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
import kotlin.system.measureTimeMillis

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
        val ids = ArrayList<Long>()
        for (trackId in 0..trackIds!!.lastIndex) {
            val id = trackIds!![trackId].id
            loge(id.toString())
            ids.add(id)
        }
        getSongListByIds(ids) {
            success.invoke(it)
        }

//        // 初始化歌单
//        // val sortStandardSongDataList = ArrayList<SortStandardSongData>()
//        standardSongDataList.clear()
//        i = 0
//        val time = measureTimeMillis {
//            read(){
//                success.invoke(standardSongDataList)
//            }
//        }
//        toast("加载歌单耗时：${time} ms")

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

    private fun getSongListByIds(ids: ArrayList<Long>, success: (ArrayList<StandardSongData>) -> Unit) {
        var idsString = ""
        for (id in 0..ids.lastIndex) {
            idsString = "$idsString${ids[id]},"
        }
        idsString = idsString.substring(0, idsString.lastIndex)
        val url = "http://music.163.com/api/song/detail/?ids=%5B${idsString}%5D"
        loge(url.toString())


        MagicHttp.OkHttpManager().newGet(url, {
            val data = Gson().fromJson(it, CompatSearchData::class.java)
            success.invoke(compatSearchDataToStandardPlaylistData(data))
        }, {

        })

    }

}

// http://music.163.com/api/song/detail/?id=1423062698&ids=%5B1423062698,521351799%5D
// http://www.pinlue.com/article/2020/07/0300/2810948441933.html



