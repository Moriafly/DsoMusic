package com.dirror.music.ui.fragment

import androidx.fragment.app.viewModels
import com.dirror.music.R
import com.dirror.music.foyou.sentence.Sentence
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.AnimationUtil
import com.dirror.music.util.runOnMainThread
import com.dirror.music.viewmodel.HomeViewModel
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
        changeSentence()
    }

    override fun initListener() {
        includeFoyou.setOnClickListener {
            changeSentence()
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

}