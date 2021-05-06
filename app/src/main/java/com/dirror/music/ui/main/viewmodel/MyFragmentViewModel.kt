package com.dirror.music.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.api.API_AUTU
import com.dirror.music.api.CloudMusicApi
import com.dirror.music.data.PlaylistData
import com.dirror.music.data.UserPlaylistData
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.netease.UserPlaylist
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.dirror.music.util.runOnMainThread
import com.google.gson.Gson
import okhttp3.FormBody

/**
 * MyFragment 的 ViewModel
 * @author Moriafly
 * @since 2021年1月21日15:23:53
 */
class MyFragmentViewModel : ViewModel() {

    // 用户歌单
    var userPlaylistList = MutableLiveData<ArrayList<PlaylistData>>()

    fun updateUserPlaylist() {
        val requestBody = FormBody.Builder()
            .add("uid", MyApplication.userManager.getCurrentUid().toString())
            .add("cookie", MyApplication.userManager.getCloudMusicCookie().toString())
            .build()
        MagicHttp.OkHttpManager().newPost(API_AUTU + "/user/playlist", requestBody, { response ->
            var userPlaylistData: UserPlaylistData? = null
            try {
                userPlaylistData = Gson().fromJson(response, UserPlaylistData::class.java)
            } catch (e: Exception) {

            }

            runOnMainThread {
                userPlaylistList.value = userPlaylistData?.playlist
            }
            // success()
        }, {
            // failure(ErrorCode.MAGIC_HTTP)
        })
    }

}