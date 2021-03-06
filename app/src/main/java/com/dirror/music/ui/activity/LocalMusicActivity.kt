package com.dirror.music.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.databinding.ActivityLocalMusicBinding
import com.dirror.music.music.local.LocalMusic
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.parseArtist

class LocalMusicActivity : BaseActivity() {

    private lateinit var binding: ActivityLocalMusicBinding

    override fun initBinding() {
        binding = ActivityLocalMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {

    }

    override fun initData() {
        scanLocalMusicByCheckPermission()
    }

    override fun initListener() {
        binding.ivScanMusic.setOnClickListener {
            scanLocalMusicByCheckPermission()
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

    @SuppressLint("SetTextI18n")
    override fun initMiniPlayer() {
        binding.miniPlayer.apply {
            root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@LocalMusicActivity) }
            ivPlayQueue.setOnClickListener { PlaylistDialog().show(supportFragmentManager, null) }
            ivStartOrPause.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
        }
        MyApplication.musicController.observe(this, { nullableController ->
            nullableController?.apply {
                getPlayingSongData().observe(this@LocalMusicActivity, { songData ->
                    songData?.let {
                        binding.miniPlayer.tvTitle.text = songData.name + " - " + songData.artists?.let { parseArtist(it) }
                    }
                })
                isPlaying().observe(this@LocalMusicActivity, {
                    binding.miniPlayer.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                })
                getPlayerCover().observe(this@LocalMusicActivity, { bitmap ->
                    binding.miniPlayer.ivCover.load(bitmap) {
                        size(ViewSizeResolver(binding.miniPlayer.ivCover))
                        error(R.drawable.ic_song_cover)
                    }
                })
            }
        })
    }

}