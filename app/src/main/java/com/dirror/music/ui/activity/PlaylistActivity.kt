package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.SongDataAdapter
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.databinding.ActivityPlaylistBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.netease.PlaylistUtil
import com.dirror.music.music.standard.data.*
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.*
import com.dirror.music.util.GlideUtil
import com.google.gson.Gson
import jp.wasabeef.glide.transformations.BlurTransformation
import java.lang.Exception

/**
 * 歌单 Activity
 * 最新要求：兼容 网易和 QQ
 */
class PlaylistActivity : BaseActivity() {

    companion object {
        const val EXTRA_PLAYLIST_SOURCE = "int_playlist_source"
        const val EXTRA_LONG_PLAYLIST_ID = "int_playlist_id"
    }

    private lateinit var binding: ActivityPlaylistBinding

    private var detailPlaylistAdapter = SongDataAdapter(this)

    private var playlistId: Long = -1L

    private var playlistSource: Int = SOURCE_NETEASE

    override fun initBinding() {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.allowEnterTransitionOverlap = true
            window.allowReturnTransitionOverlap = true
        }
        // 屏幕适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@PlaylistActivity)
        }
        val navigationHeight = if (MyApplication.config.mmkv.decodeBool(Config.PARSE_NAVIGATION, true)) {
            getNavigationBarHeight(this)
        } else {
            0
        }
        (binding.miniPlayer.root.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = navigationHeight
        }
        // 色彩
        binding.ivPlayAll.setColorFilter(ContextCompat.getColor(this, R.color.colorAppThemeColor))
        // 获取歌单来源
        playlistSource = intent.getIntExtra(EXTRA_PLAYLIST_SOURCE, SOURCE_NETEASE)
        // 获取歌单 id
        playlistId = intent.getLongExtra(EXTRA_LONG_PLAYLIST_ID, -1)

        binding.lottieLoading.repeatCount = -1
        binding.lottieLoading.playAnimation()

        initPlaylist(playlistSource, playlistId)

        var rvPlaylistScrollY = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
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

    }

    override fun initListener() {
        /**
         * 全部播放
         * 播放第一首歌
         */
        binding.clNav.setOnClickListener {
            if (detailPlaylistAdapter.itemCount != 0) {
                detailPlaylistAdapter.playFirst()
            }
            // toast(detailPlaylistAdapter.itemCount.toString())
        }

        binding.ivShare.setOnClickListener {
            toast("歌单 ID 已经成功复制到剪贴板")
            copyToClipboard(this, playlistId.toString())
        }

    }

    private fun initPlaylist(source: Int, id: Long) {
        when (source) {
            SOURCE_NETEASE -> {
                // 加载歌单信息
                initPlaylistInfo(id)
                // 加载歌单歌曲
                initPlaylistSongs(id) {
                    initRecycleView(it)
                }
            }
            SOURCE_DIRROR -> {
                val url = "https://moriafly.xyz/dirror-music/json/music.json"
                MagicHttp.OkHttpManager().newGet(url, {
                    try {
                        val playlistData = Gson().fromJson(it, StandardPlaylistData::class.java)
                        binding.tvName.text = playlistData.name
                        binding.tvDescription.text = playlistData.description
                        initRecycleView(playlistData.songs)
                    } catch (e: Exception) { }
                }, {

                })
            }
            SOURCE_LOCAL -> {
                // 我喜欢
                if (id == 0L) {
                    initPlaylistInfo(id)
                    MyFavorite.read {
                        initRecycleView(it, PLAYLIST_TAG_MY_FAVORITE)
                    }
                }
            }
        }
    }

    /**
     * 初始化歌单信息
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initPlaylistInfo(id: Long) {
        if (playlistSource == SOURCE_LOCAL && playlistId == 0L) {
            ContextCompat.getDrawable(this, R.drawable.ic_bq_love_music_filter)?.let {
                binding.ivCover.setImageBitmap(it.toBitmap())
                binding.tvName.text = getString(R.string.my_favorite_songs)
                setBackground(it.toBitmap())
            }
            return
        }
        PlaylistUtil.getPlaylistInfo(this, id) {
            it.coverImgUrl?.let { url ->
                GlideUtil.load(url) { bitmap ->
                    setBackground(bitmap)
                }
            }
            runOnMainThread {
                binding.tvName.text = it.name
                binding.tvDescription.text = it.description
            }
        }
    }

    private fun setBackground(bitmap: Bitmap) {
        runOnMainThread {
            binding.ivCover.setImageBitmap(bitmap)
            Glide.with(MyApplication.context)
                .load(bitmap)
                .placeholder(binding.ivBackground.drawable)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 10)))
                .into(binding.ivBackground)
        }
    }

    private fun initPlaylistSongs(id: Long, success: (ArrayList<StandardSongData>) -> Unit) {
        Playlist.getPlaylist(this, id, {
            success.invoke(it)
        }, {

        })
    }

    @SuppressLint("SetTextI18n")
    private fun initRecycleView(songList: ArrayList<StandardSongData>, tag: Int = PLAYLIST_TAG_NORMAL) {
        runOnMainThread {
            binding.clLoading.visibility = View.GONE
            binding.rvPlaylist.layoutManager =  LinearLayoutManager(this@PlaylistActivity)
            // 改变全局变量
            detailPlaylistAdapter = SongDataAdapter(this@PlaylistActivity, tag).apply {
                submitList(songList)
            }
            binding.rvPlaylist.adapter = detailPlaylistAdapter
            binding.tvPlayAll.text = getString(R.string.play_all, songList.size)
            binding.lottieLoading.pauseAnimation()
        }
    }

}