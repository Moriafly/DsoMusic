package com.dirror.music.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dirror.music.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.qq.Picture
import com.dirror.music.music.standard.SOURCE_NETEASE
import com.dirror.music.music.standard.SOURCE_QQ
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.dialog.UpdateDialog
import com.dirror.music.util.*
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_play.view.*

class MainActivity : BaseActivity(R.layout.activity_main) {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        checkNewVersion()
    }


    override fun initView() {
        setPlayerVisibility(false)
        // 请求广播
        MyApplication.musicBinderInterface?.sendBroadcast()

        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        blurView.setupWith(decorView.findViewById(R.id.viewPager2))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        blurViewPlay.setupWith(decorView.findViewById(R.id.viewPager2))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        // 适配状态栏
        val statusBarHeight = getStatusBarHeight(window, this) // px
        titleBar.translationY = statusBarHeight.toFloat()
        blurView.scaleY = (dp2px(56f) + statusBarHeight) / dp2px(56f)
        blurView.translationY = statusBarHeight.toFloat() / 2
        blurViewBottom.scaleY = blurView.scaleY
        blurViewBottom.translationY = statusBarHeight.toFloat() / 2

        // 适配导航栏
        val navigationBarHeight = getNavigationBarHeight(this).toFloat()
        clPlay.translationY = - navigationBarHeight
        blurViewPlay.scaleY = (dp2px(56f) + navigationBarHeight) / dp2px(56f)
        blurViewPlay.translationY = - navigationBarHeight / 2
        blurViewPlayBottom.scaleY = (dp2px(56f) + navigationBarHeight) / dp2px(56f)
        blurViewPlayBottom.translationY = - navigationBarHeight / 2

        viewPager2.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2 // 2 个页面
            }

            override fun createFragment(position: Int): Fragment {
                return FragmentUtil.getFragment(position)
            }
        }

        // viewPager2.currentItem = 1 // 默认打开首页
        ViewPager2Util.changeToNeverMode(viewPager2)

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my)
                else -> getString(R.string.home)
            }
        }.attach()

        itemPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }

        itemPlay.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }

        itemPlay.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }

        ivSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_left,
                R.anim.anim_slide_exit_right
            )
        }

    }

    override fun initListener() {
        // 搜索按钮
        ivSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(musicBroadcastReceiver)
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()
            if (song != null) {
                setPlayerVisibility(true)
                itemPlay.tvName.text = song.name
                itemPlay.tvArtist.text = song.artists?.let { parseArtist(it) }
                when (song.source) {
                    SOURCE_NETEASE -> {
                        GlideUtil.load(CloudMusic.getMusicCoverUrl(song.id?:-1L), itemPlay.ivCover, itemPlay.ivCover)
                    }
                    SOURCE_QQ -> {
                        GlideUtil.load(Picture.getMin(song.imageUrl?:""), itemPlay.ivCover, itemPlay.ivCover)
                    }
                }

            } else {
                // 隐藏底部界面
                setPlayerVisibility(false)
            }
            refreshPlayState()
        }
    }

    private fun setPlayerVisibility(state: Boolean) {
        if (state) {
            clPlay.visibility = View.VISIBLE
            blurViewPlay.visibility = View.VISIBLE
            blurViewPlayBottom.visibility = View.VISIBLE
        } else {
            clPlay.visibility = View.INVISIBLE
            blurViewPlay.visibility = View.INVISIBLE
            blurViewPlayBottom.visibility = View.INVISIBLE
        }
    }

    /**
     * 刷新播放状态
     */
    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState()!!) {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }

    /**
     * 检查新版本
     */
    private fun checkNewVersion() {
        UpdateUtil.getServerVersion({ updateData ->
            runOnMainThread {
                if (updateData.code > getVisionCode()) {
                    // 有新版
                    UpdateDialog(this).also {
                        it.showInfo(updateData)
                        it.show()
                    }
                } else {
                    // 没有新版
                    toast("已是最新版本")
                }
            }
        }, {
            toast("获取服务器版本信息失败")
            // 失败
        })
    }


}