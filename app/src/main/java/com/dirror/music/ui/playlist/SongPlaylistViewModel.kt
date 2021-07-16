package com.dirror.music.ui.playlist

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApp
import com.dirror.music.manager.User
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Api
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG_LOCAL_MY_FAVORITE = 0
const val TAG_NETEASE = 1
const val TAG_NETEASE_MY_FAVORITE = 2

class SongPlaylistViewModel : ViewModel() {

    companion object {
        const val TAG = "SongPlaylistViewModel"
    }

    var tag = MutableLiveData(TAG_NETEASE)

    var playlistTitle = MutableLiveData("")

    var playlistDescription = MutableLiveData("")

    var playlistId = MutableLiveData("")

    var playlistUrl = MutableLiveData("")

    var songList = MutableLiveData(ArrayList<StandardSongData>())

    fun update(context: Context) {
        when (tag.value) {
            TAG_NETEASE -> {
                val id = playlistId.value?.toLong()
                if (id != null) {
                    GlobalScope.launch {
                        val list = Api.getPlayListByUID(id)
                        withContext(Dispatchers.Main) {
                            if (list.isEmpty()) {
                                toast("歌单内容获取失败")
                            }
                            songList.value = list
                        }
                    }
                }
            }
            TAG_NETEASE_MY_FAVORITE -> {
                if (User.hasCookie) {
                    Log.i(TAG, "update: 开始加载我喜欢歌单 ${System.currentTimeMillis()}")
                    Playlist.getPlaylistByCookie(playlistId.value?.toLong() ?: 0L) {
                        Log.i(TAG, "update: 得到我喜欢歌单 ${System.currentTimeMillis()}")
                        setSongList(it)
                    }
                } else {
                    toast("由于网易云最新调整，UID 登录无法再查看我喜欢歌曲（其他歌单不受影响），请使用手机号登录")
                }
            }
            TAG_LOCAL_MY_FAVORITE -> {
                MyFavorite.read {
                    setSongList(it)
                }
            }
        }
    }

    fun updateInfo(context: Context) {
        when (tag.value) {
            TAG_NETEASE, TAG_NETEASE_MY_FAVORITE -> {
                GlobalScope.launch {
                    val info = Api.getPlayListInfo(playlistId.value?.toLong() ?: 0L)
                    if (info != null) {
                        withContext(Dispatchers.Main) {
                            playlistUrl.value = info.coverImgUrl ?: ""
                            playlistTitle.value = info.name ?: ""
                            playlistDescription.value = info.description ?: ""
                        }
                    }
                }
            }
            TAG_LOCAL_MY_FAVORITE -> {
                playlistTitle.value = "本地我喜欢"
                playlistDescription.value = "收藏你喜欢的本地、网易云、QQ 音乐等歌曲"
                songList.value?.let {
                    if (it.size > 0) {
                        playlistUrl.value = songList.value?.get(0)?.imageUrl ?: ""
                    }
                }
            }
        }
    }

    private fun setSongList(list: ArrayList<StandardSongData>) {
        runOnMainThread {
            songList.value = list
        }
    }

    var navigationBarHeight = MutableLiveData<Int>()

}