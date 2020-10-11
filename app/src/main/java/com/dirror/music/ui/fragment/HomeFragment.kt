package com.dirror.music.ui.fragment

import androidx.fragment.app.viewModels
import com.dirror.music.music.DirrorMusic
import com.dirror.music.R
import com.dirror.music.adapter.MusicBannerAdapter
import com.dirror.music.foyou.sentence.Sentence
import com.dirror.music.music.netease.BannerUtil
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.runOnMainThread
import com.dirror.music.viewmodel.HomeViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_foyou.*


class HomeFragment : BaseFragment() {

    private val homeViewModel by viewModels<HomeViewModel>() // 获取 Banner 数据

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initData() {

    }

    override fun initView() {


    }

    override fun initListener() {
        includeFoyou.setOnClickListener {
            Sentence.getSentence {
                runOnMainThread {
                    tvText.text = it.text
                    tvAuthor.text = it.author
                    tvSource.text = it.source
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

}