package com.dirror.music.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.BannerAdapter
import com.dirror.music.adapter.PlaylistRecommendAdapter
import com.dirror.music.foyou.sentence.Sentence
import com.dirror.music.music.netease.PlaylistRecommend
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.AnimationUtil
import com.dirror.music.util.dp
import com.dirror.music.util.runOnMainThread
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_foyou.*


class HomeFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initData() {

    }

    override fun initView() {
        changeSentence()
        refreshPlaylistRecommend()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width
        (banner.layoutParams as LinearLayout.LayoutParams).apply{
            height = ((width - 40.dp()).toFloat() / 108 * 42).toInt() + 8.dp()
        }
        initBanner()
    }

    private fun initBanner() {
        MyApplication.cloudMusicManager.getBanner({
            val bannerAdapter = BannerAdapter(it)
            runOnMainThread {
                banner.apply {
                    addBannerLifecycleObserver(this@HomeFragment) // 感知生命周期
                    adapter = bannerAdapter
                    setIndicator(CircleIndicator(context), false)
                    setLoopTime(5000) // 轮播时间
                    // setBannerGalleryMZ(20, 0.85F)
                    start()
                }
            }
            // banner 点击事件
            banner.setOnBannerListener { _, position ->
//                val bannerData = bannerAdapter.getData(position) // 选中的 Banner
//                val intent = Intent(context, SearchAlbumActivity::class.java)
//                intent.putExtra("data_recommend", bannerData.intent)
//                startActivity(intent)
//                activity?.overridePendingTransition(
//                    R.xml.activity_enter_alpha,
//                    R.xml.activity_exit_alpha
//                )
            }
        }, {

        })
    }

    override fun initListener() {
        includeFoyou.setOnClickListener {
            changeSentence()
        }

        includeFeedback.setOnClickListener {
            activity?.let {
                MyApplication.activityManager.startFeedbackActivity(it)
            }
        }
    }

    private fun changeSentence() {
        tvText.alpha = 0f
        tvAuthor.alpha = 0f
        tvSource.alpha = 0f
        Sentence.getSentence {
            runOnMainThread {
                tvText.text = it.text
                tvAuthor.text = it.author
                tvSource.text = it.source
                AnimationUtil.fadeIn(tvText, 1000, false)
                AnimationUtil.fadeIn(tvAuthor, 1000, false)
                AnimationUtil.fadeIn(tvSource, 1000, false)
            }
        }
    }

    private fun refreshPlaylistRecommend() {
        PlaylistRecommend.getPlaylistRecommend({
            runOnMainThread {
                rvPlaylistRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                rvPlaylistRecommend.adapter = PlaylistRecommendAdapter(it)
            }
        }, {

        })

    }

}