package com.dirror.music.music.netease

import android.content.Context
import android.util.Log
import com.dirror.music.api.*
import com.dirror.music.data.DetailPlaylistData
import com.dirror.music.data.DetailPlaylistInnerData
import com.dirror.music.manager.User
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.toast
import com.google.gson.Gson

object PlaylistUtil {

    private const val TAG = "PlaylistUtil"

    @Deprecated("过时")
    fun getDetailPlaylist(id: Long, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        val url = "$API_MUSIC_ELEUU/playlist/detail?id=$id"
        loge("url:$url")
        MagicHttp.OkHttpManager().newGet(url, { response ->
            val detailPlaylistData = Gson().fromJson(response, DetailPlaylistData::class.java)
            // 406 为操作频繁错误
            if (detailPlaylistData.code != 406) {
                detailPlaylistDataToStandardSongDataList(detailPlaylistData) {
                    success.invoke(it)
                }
            }
        }, {
            failure.invoke(it)
        })
    }

    @Deprecated("过时")
    private fun detailPlaylistDataToStandardSongDataList(
        detailPlaylistData: DetailPlaylistData,
        success: (ArrayList<StandardSongData>) -> Unit
    ) {
        // 获取全 id
        val trackIds = detailPlaylistData.playlist?.trackIds
        val ids = ArrayList<Long>()
        if (trackIds != null) {
            for (trackId in 0..trackIds.lastIndex) {
                val id = trackIds[trackId].id
                ids.add(id)
            }
        }
        getSongListByIds(ids) {
            success.invoke(it)
        }

    }

    @Deprecated("过时")
    private fun getSongListByIds(ids: ArrayList<Long>, success: (ArrayList<StandardSongData>) -> Unit) {
        var idsString = ""
        for (id in 0..ids.lastIndex) {
            idsString = "$idsString${ids[id]},"
        }
        if (idsString != "") {
            idsString = idsString.substring(0, idsString.lastIndex)
        }

        // val url = "${API_MUSIC_ELEUU}/song/detail/?ids=${idsString}"
        // val url = "${API_NETEASE}/song/detail/?ids=%5B${idsString}%5D&proxy=http://121.196.226.246:84"
        val url = "${API_NETEASE}/song/detail/?ids=%5B${idsString}%5D"
        Log.e(TAG, "getSongListByIds: $url", )
        MagicHttp.OkHttpManager().newGet(url, {
            val data = Gson().fromJson(it, CompatSearchData::class.java)
            if (data.code == -460) {
                toast("-460 Cheating")
            } else {
                success.invoke(compatSearchDataToStandardPlaylistData(data))
            }
        }, {

        })
    }

    /**
     * 获取歌单信息
     */
    fun getPlaylistInfo(context: Context, id: Long, success: (DetailPlaylistInnerData) -> Unit) {
        val url = "$API_AUTU/playlist/detail?id=$id&cookie=${AppConfig.cookie}"
        Log.i(TAG, "获取歌单信息 $url")
        MagicHttp.OkHttpManager().getByCache(context, url, { response ->
            try {
                val playlistInfo = Gson().fromJson(response, DetailPlaylistData::class.java).playlist
                if (playlistInfo != null) {
                    success.invoke(playlistInfo)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

}

// http://music.163.com/api/song/detail/?id=1423062698&ids=%5B1423062698,521351799%5D
// http://www.pinlue.com/article/2020/07/0300/2810948441933.html



