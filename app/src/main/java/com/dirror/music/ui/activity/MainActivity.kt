package com.dirror.music.ui.activity

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.FragmentUtil
import com.dirror.music.util.dp2px
import com.dirror.music.util.getStatusBarHeight
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
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
    }




}