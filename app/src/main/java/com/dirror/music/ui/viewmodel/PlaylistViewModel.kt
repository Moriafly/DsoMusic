package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.netease.Playlist2
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.runOnMainThread

class PlaylistViewModel: ViewModel() {

    // id
    var id = 0L

    // source
    var source = SOURCE_NETEASE

    // tag
    var tag = MutableLiveData<Int>().also {
        it.value = PLAYLIST_TAG_NORMAL
    }

    // 歌单
    var playlist = MutableLiveData<ArrayList<StandardSongData>>()

    /**
     * 更新歌单
     */
    fun updatePlaylist() {
        // 数据库
        when (source) {
            SOURCE_LOCAL -> {
                // 我喜欢的歌曲
                if (id == 0L) {
                    MyFavorite.read {
                        runOnMainThread {
                            playlist.value = it
                        }
                    }
                }
            }
            SOURCE_NETEASE -> {
                if (MyApplication.userManager.getCloudMusicCookie().isNotEmpty()) {
                    // 用户登录了用 Playlist2
                    Playlist2.get(id)
                } else {
                    // 未登录用 Playlist
                }
            }
        }

    }

    fun setTag(newTag: Int) {
        tag.value = newTag
    }

}