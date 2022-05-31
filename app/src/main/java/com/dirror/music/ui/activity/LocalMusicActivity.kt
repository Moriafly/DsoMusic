package com.dirror.music.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.R
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.databinding.ActivityLocalMusicBinding
import com.dirror.music.music.local.LocalMusic
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.playlist.SongSearchActivity
import com.dirror.music.ui.playlist.SongSearchTransmit
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import kotlin.concurrent.thread

/**
 * 本地音乐
 * @author Moriafly
 */
class LocalMusicActivity : BaseActivity() {

    companion object {
        private const val PERMISSION_REQUEST_READ_AND_WRITE = 1

        class InnerViewModel: ViewModel() {
            val songList = MutableLiveData<ArrayList<StandardSongData>>()

            fun scanLocalMusic(context: Context) {
                LocalMusic.scanLocalMusic(context, {
                    runOnMainThread {
//            binding.titleBar.setTitleBarText("本地音乐(${it.size})")
                        songList.value = it
//                        songList = it
//                        adapter.submitList(it)
//                        itemSearchAdapter.text = "搜索此列表歌曲 ${it.size} 首"
//                        binding.rvSongs.scrollToPosition(0)
                    }
                }, {

                })
            }
        }

    }

    private lateinit var binding: ActivityLocalMusicBinding

    private val localMusicViewModel: InnerViewModel by viewModels()

    private lateinit var songAdapter: SongAdapter

    override fun initBinding() {
        binding = ActivityLocalMusicBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        songAdapter = SongAdapter {
            SongMenuDialog(this, this, it) {
                toast("不支持删除")
            }.show()
        }
        if (getPermissionEnabled()) {
            localMusicViewModel.scanLocalMusic(this)
        } else {
            requestPermission()
        }
    }

    override fun initView() {
        with(binding) {
            rvSong.layoutManager = LinearLayoutManager(this@LocalMusicActivity)
            rvSong.adapter = songAdapter
        }
    }

    override fun initListener() {
        with(binding) {
            ivSearch.setOnClickListener {
                thread {
                    localMusicViewModel.songList.value?.let { it2 ->
                        SongSearchTransmit.songList = it2
                        runOnMainThread {
                            startActivity(Intent(this@LocalMusicActivity, SongSearchActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    override fun initObserver() {
        localMusicViewModel.songList.observe(this) {
            songAdapter.submitList(it)
            binding.titleBarLayout.setTitleBarText(getString(R.string.local_music) + "(${it.size})")
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_READ_AND_WRITE
            )
        }
    }

    private fun getPermissionEnabled(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_READ_AND_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                localMusicViewModel.scanLocalMusic(this)
            } else {
                toast("拒绝权限无法扫描本地音乐")
            }
            else -> {

            }
        }
    }

//    thread {
//        SongSearchTransmit.songList = songList
//        runOnMainThread {
//            startActivity(Intent(requireContext(), SongSearchActivity::class.java))
//        }
//    }
}