package com.dirror.music.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.databinding.ActivityLocalMusicBinding
import com.dirror.music.music.local.LocalMusic
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.parseArtist

class LocalMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalMusicBinding

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalMusicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        initListener()

        scanLocalMusicByCheckPermission()
    }

    private fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        // 请求广播
        MyApplication.musicBinderInterface?.sendBroadcast()
    }

    private fun initListener() {

        binding.ivScanMusic.setOnClickListener {
            scanLocalMusicByCheckPermission()
        }

        // 播放栏
        binding.includePlayer.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }

        binding.includePlayer.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }

        binding.includePlayer.root.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
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
            binding.rvLocalMusic.adapter = DetailPlaylistAdapter(it, this)
            binding.rvLocalMusic.layoutManager = LinearLayoutManager(this)
            binding.titleBar.setTitleBarText("本地音乐(${it.size})")
        }, {

        })
    }

    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        // 接收
        override fun onReceive(context: Context, intent: Intent) {
            refreshLayoutPlay()
            refreshPlayState()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(musicBroadcastReceiver)
    }

    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_mini_player_play)
        }
    }

    private fun refreshLayoutPlay() {
        MyApplication.musicBinderInterface?.getNowSongData()?.let { standardSongData ->
            binding.includePlayer.tvName.text = standardSongData.name
            binding.includePlayer.tvArtist.text = standardSongData.artists?.let { parseArtist(it) }
            SongPicture.getSongPicture(this, standardSongData, SongPicture.TYPE_LARGE) {
                binding.includePlayer.ivCover.setImageBitmap(it)
            }
        }
    }

}