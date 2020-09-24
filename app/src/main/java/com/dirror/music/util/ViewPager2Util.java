package com.dirror.music.util;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS;
import static android.view.View.OVER_SCROLL_NEVER;

/**
 * 消除 ViewPager2 的边界阴影
 * @author A lonely cat
 * @链接 https://www.sunofbeach.net/a/1286598945774018560
 */
public class ViewPager2Util {

    /**
     *  Change to OVER_SCROLL_NEVER Mode
     */
    public static ViewPager2 changeToNeverMode(ViewPager2 viewPager2) {
        return changeOverScrollMode(viewPager2, OVER_SCROLL_NEVER);
    }

    /**
     *  Change to OVER_SCROLL_ALWAYS Mode
     */
    public static ViewPager2 changeToAlwaysMode(ViewPager2 viewPager2) {
        return changeOverScrollMode(viewPager2, OVER_SCROLL_ALWAYS);
    }

    /**
     * Change to OVER_SCROLL_IF_CONTENT_SCROLLS Mode
     */
    public static ViewPager2 changeToIfContentScrollsMode(ViewPager2 viewPager2) {
        return changeOverScrollMode(viewPager2, OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    /**
     * Change OverScrollMode
     */
    public static ViewPager2 changeOverScrollMode(ViewPager2 viewPager2, int overMode) {
        View childView = viewPager2.getChildAt(0);
        if (childView instanceof RecyclerView) {
            childView.setOverScrollMode(overMode);
        }
        return viewPager2;
    }
}
