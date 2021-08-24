package com.dirror.music.service

import android.content.ContentUris
import android.net.Uri
import com.dirror.music.MyApp
import com.dirror.music.data.LyricViewData
import com.dirror.music.music.kuwo.SearchSong
import com.dirror.music.music.netease.SongUrl
import com.dirror.music.music.qq.PlayUrl
import com.dirror.music.music.standard.SearchLyric
import com.dirror.music.music.standard.data.*
import com.dirror.music.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 获取歌曲 URL
 */
object ServiceSongUrl {

    inline fun getUrlProxy(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        getUrl(song) {
            GlobalScope.launch { withContext(Dispatchers.Main) {
                success.invoke(it)
            } }

        }
    }

    inline fun getUrl(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        when (song.source) {
            SOURCE_NETEASE -> {
                GlobalScope.launch {
                    if (song.neteaseInfo?.pl == 0) {
                        if (MyApp.mmkv.decodeBool(Config.AUTO_CHANGE_RESOURCE)) {
                            GlobalScope.launch {
                                val url = getUrlFromOther(song)
                                if (url.isEmpty()) {
                                    toast("自动换源失败，未找到可用源")
                                }
                                success.invoke(url)
                            }
                        } else {
                            success.invoke(null)
                        }
                    } else {
                        var url = ""
                        if (url.isEmpty()) url = SongUrl.getSongUrlN(song.id?:"")
                        success.invoke(url)
                    }
                }
            }
            SOURCE_QQ -> {
                GlobalScope.launch {
                    success.invoke(PlayUrl.getPlayUrl(song.id?:""))
                }
            }
            SOURCE_DIRROR -> {
                GlobalScope.launch {
                    success.invoke(song.dirrorInfo?.url)
                }
            }
            SOURCE_KUWO -> {
                GlobalScope.launch {
                    val url = SearchSong.getUrl(song.id?:"")
                    success.invoke(url)
                }
            }
            SOURCE_NETEASE_CLOUD -> {
                SongUrl.getSongUrlCookie(song.id?:"") {
                    success.invoke(it)
                }
            }
            else -> success.invoke(null)
        }
    }

    fun getLyric(song: StandardSongData, success: (LyricViewData) -> Unit) {
        if (song.source == SOURCE_NETEASE) {
            MyApp.cloudMusicManager.getLyric(song.id?.toLong() ?: 0) { lyric ->
                runOnMainThread {
                    val l = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                    success.invoke(l)
                }
            }
        } else {
            SearchLyric.getLyricString(song) { string ->
                runOnMainThread {
                    success.invoke(LyricViewData(string, ""))
                }
            }
        }
    }

    suspend fun getUrlFromOther(song: StandardSongData) : String {
        Api.getFromKuWo(song)?.apply {
            SearchSong.getUrl(id?:"").let {
                if (it.isNotEmpty()) {
                    toast("换源到酷我[$name-${getArtistName(artists)}]成功")
                }
                return it
            }
        }
        Api.getFromQQ(song)?.apply {
           PlayUrl.getPlayUrl(id?:"").let {
               if (it.isNotEmpty()) {
                   toast("换源到QQ[$name-${getArtistName(artists)}]成功")
               }
               return it
           }


        }
        return ""
    }

    private fun getArtistName(artists:List<StandardSongData.StandardArtistData>?) : String {
        val sb = StringBuilder()
        artists?.forEach {
            if (sb.isNotEmpty()) {
                sb.append(" ")
            }
            sb.append(it.name)
        }
        return sb.toString()
    }

}