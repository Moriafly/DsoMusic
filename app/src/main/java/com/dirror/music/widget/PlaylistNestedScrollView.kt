package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class PlaylistNestedScrollView: NestedScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val scrollInterface: ScrollInterface? = null

    interface ScrollInterface {
        fun onScrollChange(scrollX: Int, ScrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        scrollInterface?.onScrollChange(l, t, oldl, oldt)
        super.onScrollChanged(l, t, oldl, oldt)
    }
}