package com.dirror.music.ui.playlist

import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.SongDataAdapter
import com.dirror.music.databinding.ActivityPlaylistBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.playlist.SongPlaylistViewModel.Companion.TAG_NETEASE
import com.dirror.music.util.*
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 3.0
 * 歌曲歌单
 * 融合
 */
class SongPlaylistActivity: BaseActivity() {

    companion object {
        const val EXTRA_TAG = "extra_tag"
        const val EXTRA_PLAYLIST_ID = "extra_playlist_id"
    }

    private lateinit var binding: ActivityPlaylistBinding

    private val songPlaylistViewModel: SongPlaylistViewModel by viewModels()

    val adapter = SongDataAdapter(this)

    override fun initBinding() {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        songPlaylistViewModel.tag.value = intent.getIntExtra(EXTRA_TAG, TAG_NETEASE)
        songPlaylistViewModel.playlistId.value = intent.getStringExtra(EXTRA_PLAYLIST_ID)
    }

    override fun initView() {
        // 屏幕适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@SongPlaylistActivity)
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

        binding.lottieLoading.repeatCount = -1
        binding.lottieLoading.playAnimation()

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

    override fun initObserver() {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = adapter
        songPlaylistViewModel.apply {
            songList.observe(this@SongPlaylistActivity, {
                 if (it.size > 0 || tag.value == SongPlaylistViewModel.TAG_LOCAL_MY_FAVORITE) {
                    binding.clLoading.visibility = View.GONE
                    binding.lottieLoading.pauseAnimation()
                 }
                binding.tvPlayAll.text = getString(R.string.play_all, it.size)
                adapter.submitList(it)
                if (songPlaylistViewModel.tag.value == SongPlaylistViewModel.TAG_LOCAL_MY_FAVORITE) {
                    songPlaylistViewModel.updateInfo(this@SongPlaylistActivity)
                }
            })
            playlistTitle.observe(this@SongPlaylistActivity, {
                binding.tvName.text = it
            })
            playlistDescription.observe(this@SongPlaylistActivity, {
                binding.tvDescription.text = it
            })
            playlistId.observe(this@SongPlaylistActivity, {
                songPlaylistViewModel.update(this@SongPlaylistActivity)
                songPlaylistViewModel.updateInfo(this@SongPlaylistActivity)
            })
            playlistUrl.observe(this@SongPlaylistActivity, {
                GlideUtil.load(it) { bitmap ->
                    runOnMainThread {
                        binding.ivCover.setImageBitmap(bitmap)
                        Glide.with(MyApplication.context)
                            .load(bitmap)
                            .placeholder(binding.ivBackground.drawable)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 10)))
                            .into(binding.ivBackground)
                    }
                }
            })
        }

    }

    override fun initListener() {
        /**
         * 全部播放
         * 播放第一首歌
         */
        binding.clNav.setOnClickListener {
            AnimationUtil.click(binding.ivPlayAll)
            if (adapter.itemCount != 0) {
                adapter.playFirst()
            }
            // toast(detailPlaylistAdapter.itemCount.toString())
        }
    }


}