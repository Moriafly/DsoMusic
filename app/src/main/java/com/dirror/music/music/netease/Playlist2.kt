package com.dirror.music.music.netease

import com.dirror.music.MyApplication
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.netease.data.NeteaseSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import okhttp3.FormBody

/**
 * 网易云歌单解析为 ArrayList<StandardSongData>
 * 取代原来的 Playlist 不采取 Compat 方法
 * @author Moriafly
 * @since 2021年1月5日11:09:32
 * @新 [Playlist]
 */
@Deprecated("过时")
object Playlist2 {

    private const val API = "https://music.163.com/api/v6/playlist/detail"
    /**
     * 获取歌单
     * @param id 歌单 id
     */
    fun get(id: Long) {
        val requestBody = FormBody.Builder()
            .add("id", id.toString())
            .add("n", "100000")
            .add("s", "8")
            .add("crypto", "api")
            .add("cookie", MyApplication.userManager.getCloudMusicCookie())
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .build()
        MagicHttp.OkHttpManager().newPost(API, requestBody) {
            loge(it, "Playlist2")
        }
    }

    data class NeteasePlaylistData(
        val code: Int,
        val playlist: PlaylistData,
        val privileges: ArrayList<CompatSearchData.PrivilegesData>,
    ) {
        data class PlaylistData(
            val tracks: ArrayList<CompatSearchData.CompatSearchSongData>
        )
    }

}