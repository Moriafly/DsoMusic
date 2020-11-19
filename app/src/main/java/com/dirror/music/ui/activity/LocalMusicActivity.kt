package com.dirror.music.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.databinding.ActivityLocalMusicBinding
import com.dirror.music.music.local.LocalMusic

class LocalMusicActivity : AppCompatActivity() {

    companion object {

    }

    private lateinit var binding: ActivityLocalMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalMusicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initListener()

        scanLocalMusicByCheckPermission()
    }

    private fun initListener() {

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
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            scanLocalMusic()
        }
    }

    private fun scanLocalMusic() {
        LocalMusic.scanLocalMusic(this, {
            binding.rvLocalMusic.adapter = DetailPlaylistAdapter(it)
            binding.rvLocalMusic.layoutManager = LinearLayoutManager(this)
        }, {

        })
    }



}