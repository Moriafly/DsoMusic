package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.databinding.ActivityPlaylistBinding
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.viewmodel.PlaylistViewModel
import com.dirror.music.util.*
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 新版 Playlist
 */
class PlaylistActivity2: BaseActivity() {

    companion object {
        const val EXTRA_PLAYLIST_SOURCE = "int_playlist_source"
        const val EXTRA_LONG_PLAYLIST_ID = "int_playlist_id"
        const val EXTRA_INT_TAG = "int_tag"
    }

    private lateinit var binding: ActivityPlaylistBinding

    // 音乐广播接收
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver
    private lateinit var updatePlaylistReceiver: UpdatePlaylistReceiver

    private var detailPlaylistAdapter = DetailPlaylistAdapter(ArrayList(), this)

    private val playlistViewModel: PlaylistViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        // 屏幕适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@PlaylistActivity2)
        }
        (binding.includePlay.root.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = getNavigationBarHeight(this@PlaylistActivity2)
        }
        // 色彩
        binding.ivPlayAll.setColorFilter(getColor(R.color.colorAppThemeColor))

        // 获取歌单来源
        playlistViewModel.source = intent.getIntExtra(EXTRA_PLAYLIST_SOURCE, SOURCE_NETEASE)
        // 获取歌单 id
        playlistViewModel.id = intent.getLongExtra(EXTRA_LONG_PLAYLIST_ID, 0L)
        // 获取 tag
        playlistViewModel.tag.value = intent.getIntExtra(EXTRA_INT_TAG, PLAYLIST_TAG_NORMAL)

        binding.lottieLoading.repeatCount = -1
        binding.lottieLoading.playAnimation()

        // 请求更新
        playlistViewModel.updatePlaylist()

        binding.rvPlaylist.layoutManager =  LinearLayoutManager(this@PlaylistActivity2)

        var rvPlaylistScrollY = 0
        binding.rvPlaylist.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            rvPlaylistScrollY += oldScrollY
            if (rvPlaylistScrollY < 0) {
                if (binding.titleBar.text == getString(R.string.playlist)) {
                    binding.titleBar.setTitleBarText(binding.tvName.text.toString())
                }
            } else {
                binding.titleBar.setTitleBarText(getString(R.string.playlist))
            }
        }
    }

    override fun initBroadcastReceiver() {
        var intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        intentFilter = IntentFilter()
        intentFilter.addAction(SongMenuDialog.BROADCAST_UPDATE_PLAYLIST)
        updatePlaylistReceiver = UpdatePlaylistReceiver()
        registerReceiver(updatePlaylistReceiver, intentFilter) // 注册接收器
    }

    override fun initListener() {
        binding.apply {
            includePlay.root.setOnClickListener {
                startActivity(Intent(this@PlaylistActivity2, PlayerActivity::class.java))
                overridePendingTransition(
                    R.anim.anim_slide_enter_bottom,
                    R.anim.anim_no_anim
                )
            }
            includePlay.ivPlay.setOnClickListener {
                MyApplication.musicBinderInterface?.changePlayState()
            }
            includePlay.ivPlaylist.setOnClickListener {
                PlaylistDialog(this@PlaylistActivity2).show()
            }
            // 全部播放 播放第一首歌
            clNav.setOnClickListener {
                if (detailPlaylistAdapter.itemCount != 0) {
                    detailPlaylistAdapter.playFirst()
                }
            }
            ivShare.setOnClickListener {
                toast("歌单 ID 已经成功复制到剪贴板")
                copyToClipboard(this@PlaylistActivity2, playlistViewModel.id.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {
        playlistViewModel.apply {
            playlist.observe(this@PlaylistActivity2, {
                detailPlaylistAdapter = DetailPlaylistAdapter(it, this@PlaylistActivity2, tag.value)
                binding.rvPlaylist.adapter = detailPlaylistAdapter
                binding.tvPlayAll.text = "播放全部(${it.size})"
                binding.clLoading.visibility = View.GONE
                binding.lottieLoading.pauseAnimation()
            })
            tag.observe(this@PlaylistActivity2, {
                if (tag.value == PLAYLIST_TAG_MY_FAVORITE) {
                    binding.tvName.text = getString(R.string.my_favorite_songs)
                    ContextCompat.getDrawable(this@PlaylistActivity2, R.drawable.ic_bq_love_music_filter)?.let { it1 ->
                        setPicture(it1.toBitmap())
                    }
                }
            })
        }
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        // 接收
        override fun onReceive(context: Context, intent: Intent) {
            refreshLayoutPlay()
            refreshPlayState()
        }
    }

    inner class UpdatePlaylistReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            playlistViewModel.updatePlaylist()
        }
    }

    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            binding.includePlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            binding.includePlay.ivPlay.setImageResource(R.drawable.ic_mini_player_play)
        }
    }

    /**
     * 刷新下方播放框
     * 可能导致 stick 丢失
     */
    private fun refreshLayoutPlay() {
        MyApplication.musicBinderInterface?.getNowSongData()?.let { standardSongData ->
            binding.includePlay.tvName.text = standardSongData.name
            binding.includePlay.tvArtist.text = standardSongData.artists?.let { parseArtist(it) }
            SongPicture.getSongPicture(standardSongData, SongPicture.TYPE_LARGE) {
                binding.includePlay.ivCover.setImageBitmap(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refreshLayoutPlay()
        refreshPlayState()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑
        unregisterReceiver(musicBroadcastReceiver)
        unregisterReceiver(updatePlaylistReceiver)
    }

    /**
     * 设置图片
     */
    private fun setPicture(bitmap: Bitmap) {
        runOnMainThread {
            binding.ivCover.setImageBitmap(bitmap)
            Glide.with(MyApplication.context)
                .load(bitmap)
                .placeholder(binding.ivBackground.drawable)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 10)))
                .into(binding.ivBackground)
        }
    }


}