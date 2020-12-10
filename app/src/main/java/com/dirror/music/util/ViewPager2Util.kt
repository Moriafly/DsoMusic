package com.dirror.music.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * 消除 ViewPager2 的边界阴影
 * @author A lonely cat
 * @链接 https://www.sunofbeach.net/a/1286598945774018560
 */
object ViewPager2Util {
    /**
     * Change to OVER_SCROLL_NEVER Mode
     */
    fun changeToNeverMode(viewPager2: ViewPager2): ViewPager2 {
        return changeOverScrollMode(viewPager2, View.OVER_SCROLL_NEVER)
    }

    /**
     * Change to OVER_SCROLL_ALWAYS Mode
     */
    fun changeToAlwaysMode(viewPager2: ViewPager2): ViewPager2 {
        return changeOverScrollMode(viewPager2, View.OVER_SCROLL_ALWAYS)
    }

    /**
     * Change to OVER_SCROLL_IF_CONTENT_SCROLLS Mode
     */
    fun changeToIfContentScrollsMode(viewPager2: ViewPager2): ViewPager2 {
        return changeOverScrollMode(viewPager2, View.OVER_SCROLL_IF_CONTENT_SCROLLS)
    }

    /**
     * Change OverScrollMode
     */
    fun changeOverScrollMode(viewPager2: ViewPager2, overMode: Int): ViewPager2 {
        val childView = viewPager2.getChildAt(0)
        (childView as? RecyclerView)?.overScrollMode = overMode
        return viewPager2
    }
}