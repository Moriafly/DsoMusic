package com.dirror.music.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.adapter.BannerAdapter
import com.dirror.music.adapter.PlaylistRecommendAdapter
import com.dirror.music.databinding.FragmentHomeBinding
import com.dirror.music.foyou.sentence.Sentence
import com.dirror.music.music.netease.PlaylistRecommend
import com.dirror.music.music.standard.data.SOURCE_DIRROR
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.AnimationUtil
import com.dirror.music.util.dp
import com.dirror.music.util.runOnMainThread
import com.youth.banner.indicator.CircleIndicator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width
        initBanner()
        (binding.banner.layoutParams as LinearLayout.LayoutParams).apply {
            height = ((width - 40.dp()).toFloat() / 108 * 42).toInt() + 8.dp()
        }
        initView()
        initListener()
    }



    private fun initView() {
        changeSentence()
        refreshPlaylistRecommend()
    }

    private fun initBanner() {
        MyApplication.cloudMusicManager.getBanner({
            val bannerAdapter = BannerAdapter(it)
            runOnMainThread {
                binding.banner.apply {
                    addBannerLifecycleObserver(this@HomeFragment) // 感知生命周期
                    adapter = bannerAdapter
                    setIndicator(CircleIndicator(context), false)
                    setLoopTime(5000) // 轮播时间
                    // setBannerGalleryMZ(20, 0.85F)
                    start()
                }
            }
            // banner 点击事件
            binding.banner.setOnBannerListener { _, position ->
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

    private fun initListener() {
        binding.includeFoyou.root.setOnClickListener {
            changeSentence()
        }

        binding.includeFeedback.root.setOnClickListener {
            activity?.let {
                MyApplication.activityManager.startFeedbackActivity(it)
            }
        }

        binding.clDso.setOnClickListener {
            val intent = Intent(this.context, PlaylistActivity::class.java)
            intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_SOURCE, SOURCE_DIRROR)
            intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, 0)
            startActivity(intent)
        }
    }

    private fun changeSentence() {
        binding.includeFoyou.tvText.alpha = 0f
        binding.includeFoyou.tvAuthor.alpha = 0f
        binding.includeFoyou.tvSource.alpha = 0f
        Sentence.getSentence {
            runOnMainThread {
                binding.includeFoyou.tvText.text = it.text
                binding.includeFoyou.tvAuthor.text = it.author
                binding.includeFoyou.tvSource.text = it.source
                AnimationUtil.fadeIn(binding.includeFoyou.tvText, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvAuthor, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvSource, 1000, false)
            }
        }
    }

    private fun refreshPlaylistRecommend() {
        PlaylistRecommend.getPlaylistRecommend({
            runOnMainThread {
                binding.rvPlaylistRecommend.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                // binding.rvPlaylistRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvPlaylistRecommend.adapter = PlaylistRecommendAdapter(it)
            }
        }, {

        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

}