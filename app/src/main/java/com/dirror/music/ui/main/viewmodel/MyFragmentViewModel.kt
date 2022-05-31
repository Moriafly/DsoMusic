package com.dirror.music.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.PlaylistData
import com.dirror.music.data.UserPlaylistData
import com.dirror.music.manager.User
import com.dirror.music.util.AppConfig
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.Utils
import com.dirror.music.util.toast
import com.dso.ext.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * MyFragment 的 ViewModel
 * @author Moriafly
 * @since 2021年1月21日15:23:53
 */
class MyFragmentViewModel : ViewModel() {

    // 用户歌单
    var userPlaylistList = MutableLiveData<ArrayList<PlaylistData>>()

    fun updateUserPlaylist(useCache: Boolean) {
        if (User.uid != 0L) {
            val uid = User.uid.toString()
            viewModelScope.launch {
                HttpUtils.get("${User.neteaseCloudMusicApi}/user/playlist?uid=$uid&cookie=${AppConfig.cookie}", UserPlaylistData::class.java, useCache)?.apply {
                    withContext(Dispatchers.Main) {
                        userPlaylistList.value = playlist.toArrayList()
                    }
                    if (useCache) {
                        updateUserPlaylist(false)
                    }
                }
            }
        }
    }

}