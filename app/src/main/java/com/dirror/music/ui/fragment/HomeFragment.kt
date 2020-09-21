package com.dirror.music.ui.fragment

import androidx.fragment.app.viewModels
import com.dirror.music.R
import com.dirror.music.adapter.MusicBannerAdapter
import com.dirror.music.music.BannerUtil
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.loge
import com.dirror.music.util.runOnMainThread
import com.dirror.music.viewmodel.HomeViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment() {

    private val homeViewModel by viewModels<HomeViewModel>() // 获取 Banner 数据

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initData() {

    }

    override fun initView() {
        BannerUtil.getBanner { list ->
            runOnMainThread {
                banner.apply {
                    addBannerLifecycleObserver(this@HomeFragment) // 感知生命周期
                    adapter = MusicBannerAdapter(list)
                    setIndicator(CircleIndicator(context), false)
                    setLoopTime(5000) // 轮播时间
                    setBannerGalleryMZ(20, 0.85F)
                    start()
                }
            }
        }

//        homeViewModel.getBannerDataList()
//        homeViewModel.bannerDataListLive.observe(viewLifecycleOwner) { list ->

 //       }

        // banner 点击事件
        banner.setOnBannerListener { _, position ->
            // homeViewModel.getBannerDataList()
//            val bannerData = bannerAdapter.getData(position) // 选中的 Banner
//            val intent = Intent(context, SearchAlbumActivity::class.java)
//            intent.putExtra("data_recommend", bannerData.intent)
//            startActivity(intent)
//            activity?.overridePendingTransition(
//                R.xml.activity_enter_alpha,
//                R.xml.activity_exit_alpha
//            )
        }
    }

    override fun onStart() {
        super.onStart()

    }

}