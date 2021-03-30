package com.dirror.music.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

class LocalMusicActivity : BaseActivity() {

    private lateinit var binding: ActivityLocalMusicBinding

    private var songList = ArrayList<StandardSongData>()

    override fun initBinding() {
        binding = ActivityLocalMusicBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {

    }

    override fun initData() {
        scanLocalMusicByCheckPermission()
    }

    override fun initListener() {
        with(binding) {
            ivSearch.setOnClickListener {
                thread {
                    SongSearchTransmit.songList = songList
                    runOnMainThread {
                        startActivity(Intent(this@LocalMusicActivity, SongSearchActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanLocalMusic()
            } else {
                Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }

    private fun scanLocalMusicByCheckPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            scanLocalMusic()
        }
    }

    private fun scanLocalMusic() {
        LocalMusic.scanLocalMusic(this, {
            val songAdapter = SongAdapter() { songData ->
                SongMenuDialog(this, this, songData) {
                    toast("不支持删除")
                }.show()
            }
            binding.rvLocalMusic.adapter = songAdapter
            binding.rvLocalMusic.layoutManager = LinearLayoutManager(this)
            binding.titleBar.setTitleBarText("本地音乐(${it.size})")
            songList = it
            songAdapter.submitList(it)
        }, {

        })
    }

}