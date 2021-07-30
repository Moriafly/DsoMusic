package com.dirror.music.ui.playlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.data.SearchType
import com.dirror.music.manager.User
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Api
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import com.dso.ext.toArrayList
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

    /**
     * 导航栏高度
     */
    val navigationBarHeight = MutableLiveData<Int>()

    var tag = MutableLiveData(TAG_NETEASE)

    var playlistTitle = MutableLiveData("")

    var playlistDescription = MutableLiveData("")

    var playlistId = MutableLiveData("")

    var playlistUrl = MutableLiveData("")

    var songList = MutableLiveData(ArrayList<StandardSongData>())

    var type = MutableLiveData(SearchType.PLAYLIST)

    fun update() {
        when (tag.value) {
            TAG_NETEASE -> {
                val id = playlistId.value?.toLong()
                if (id != null) {
                    GlobalScope.launch {
                        when(type.value) {
                            SearchType.PLAYLIST -> {
                                val list = Api.getPlayList(id)
                                withContext(Dispatchers.Main) {
                                    if (list.isEmpty()) {
                                        toast("歌单内容获取失败")
                                    }
                                    songList.value = list
                                }
                            }
                            SearchType.ALBUM -> {
                                Api.getAlbumSongs(id)?.let {
                                    withContext(Dispatchers.Main) {
                                        songList.value = it.songs.toArrayList()
                                        playlistUrl.value = it.album.picUrl
                                        playlistTitle.value = it.album.name
                                        playlistDescription.value = it.album.description
                                    }
                                }
                            }
                            SearchType.SINGER -> {
                                Api.getSingerSongs(id)?.let {
                                    withContext(Dispatchers.Main) {
                                        songList.value = it.songs.toArrayList()
                                        playlistUrl.value = it.singer.picUrl
                                        playlistTitle.value = it.singer.name
                                        playlistDescription.value = it.singer.briefDesc
                                    }
                                }
                            }
                            else -> { }
                        }

                    }
                }
            }
            TAG_NETEASE_MY_FAVORITE -> {
                if (User.hasCookie) {
                    Log.i(TAG, "update: 开始加载我喜欢歌单 ${System.currentTimeMillis()}")
                    GlobalScope.launch {
                        playlistId.value?.toLong()?.let {
                            Api.getPlayList(it).let { list ->
                                withContext(Dispatchers.Main) {
                                    if (list.isEmpty()) {
                                        toast("歌单内容获取失败")
                                    }
                                    songList.value = list
                                }
                            }
                        }

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

    /**
     * 更新信息
     */
    fun updateInfo() {
        if (type.value != SearchType.PLAYLIST) {
            return
        }
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

}