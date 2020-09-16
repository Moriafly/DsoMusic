package com.dirror.music.ui.activity

import android.content.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.*
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_play.view.*


class MainActivity : BaseActivity() {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }


    override fun initView() {

        val statusBarHeight = getStatusBarHeight(window, this) // px
        titleBar.translationY = statusBarHeight.toFloat()
        blurView.scaleY = (dp2px(56f) + statusBarHeight).toFloat() / dp2px(56f)
        blurView.translationY = statusBarHeight.toFloat() / 2
        blurViewBottom.scaleY = blurView.scaleY
        blurViewBottom.translationY = statusBarHeight.toFloat() / 2

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


        viewPager2.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2 // 2 个页面
            }

            override fun createFragment(position: Int): Fragment {
                return FragmentUtil.getFragment(position)
            }
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "我的"
                else -> "首页"
            }
        }.attach()

        itemPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
        }

        itemPlay.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }

        ivSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicBroadcastReceiver)
        // 解绑
//        unbindService(musicConnection)
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()?.songs?.get(0)
            if (song != null) {
                itemPlay.tvName.text = song.name
                itemPlay.tvArtist.text = parseArtist(song.ar)
                GlideUtil.load(song.al.picUrl, itemPlay.ivCover)
            }
            refreshPlayState()
        }
    }

    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState()!!) {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }


}