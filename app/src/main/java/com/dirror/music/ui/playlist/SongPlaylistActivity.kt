package com.dirror.music.ui.playlist

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.R
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.data.SearchType
import com.dirror.music.databinding.ActivityPlaylistBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.*
import kotlin.concurrent.thread

/**
 * 3.0
 * 歌曲歌单
 * 融合
 */
class SongPlaylistActivity: BaseActivity() {

    companion object {
        const val EXTRA_TAG = "extra_tag"
        const val EXTRA_ID = "extra_playlist_id"
        const val EXTRA_TYPE = "extra_type"
    }

    private lateinit var binding: ActivityPlaylistBinding

    private val songPlaylistViewModel: SongPlaylistViewModel by viewModels()

    val adapter = SongAdapter {
        SongMenuDialog(this, this, it) {
            if (songPlaylistViewModel.tag.value == TAG_LOCAL_MY_FAVORITE) {
                MyFavorite.deleteById(it.id ?: "")
                songPlaylistViewModel.update()
                toast("删除成功")
            } else {
                toast("不支持删除")
            }
        }.show()
    }

    override fun initBinding() {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            songPlaylistViewModel.navigationBarHeight.value = insets.systemWindowInsetBottom
            insets
        }
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        songPlaylistViewModel.tag.value = intent.getIntExtra(EXTRA_TAG, TAG_NETEASE)
        songPlaylistViewModel.playlistId.value = intent.getStringExtra(EXTRA_ID)
        songPlaylistViewModel.type.value = intent.getSerializableExtra(EXTRA_TYPE) as? SearchType ?:SearchType.PLAYLIST
    }

    override fun initView() {
        // 屏幕适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@SongPlaylistActivity)
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
        val layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.layoutManager = layoutManager
        binding.rvPlaylist.adapter = adapter
        songPlaylistViewModel.apply {
            songList.observe(this@SongPlaylistActivity, {
                 if (it.size > 0 || tag.value == TAG_LOCAL_MY_FAVORITE) {
                    binding.clLoading.visibility = View.GONE
                    binding.lottieLoading.pauseAnimation()
                 }
                binding.tvPlayAll.text = getString(R.string.play_all, it.size)
                val sizeChange = adapter.itemCount != it.size
                val pos = layoutManager.findFirstVisibleItemPosition()
                val top = layoutManager.getChildAt(0)?.top?.apply { this - binding.rvPlaylist.paddingTop }  ?: 0
                adapter.submitList(it)
                if (songPlaylistViewModel.tag.value == TAG_LOCAL_MY_FAVORITE) {
                    songPlaylistViewModel.updateInfo()
                }
                if (sizeChange && pos >= 0) {//keep scroll pos
                    binding.rvPlaylist.post { layoutManager.scrollToPositionWithOffset(pos, top) }
                }
            })
            playlistTitle.observe(this@SongPlaylistActivity, {
                binding.tvName.text = it
            })
            playlistDescription.observe(this@SongPlaylistActivity, {
                binding.tvDescription.text = it
            })
            playlistId.observe(this@SongPlaylistActivity, {
                songPlaylistViewModel.update()
                songPlaylistViewModel.updateInfo()
            })
            playlistUrl.observe(this@SongPlaylistActivity, {
                if (it != null) {
                    binding.ivCover.load(it) {
                        size(ViewSizeResolver(binding.ivCover))
                        crossfade(300)
                    }
                    binding.ivBackground.load(it) {
                        size(ViewSizeResolver(binding.ivBackground))
                        transformations(coil.transform.BlurTransformation(this@SongPlaylistActivity, 25f, 10f))
                        crossfade(300)
                    }
                }
            })
            navigationBarHeight.observe(this@SongPlaylistActivity, {
                (binding.miniPlayer.root.layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomMargin = it
                }
            })
        }

    }

    override fun initListener() {
        with(binding) {
            /**
             * 全部播放
             * 播放第一首歌
             */
            clNav.setOnClickListener {
                AnimationUtil.click(binding.ivPlayAll)
                if (adapter.itemCount != 0) {
                    adapter.playFirst()
                }
            }
            ivSearch.setOnClickListener {
                thread {
                    songPlaylistViewModel.songList.value?.let {
                        SongSearchTransmit.songList = it
                            runOnMainThread {
                                startActivity(Intent(this@SongPlaylistActivity, SongSearchActivity::class.java))
                            }
                    }

                }
            }
        }
    }


}