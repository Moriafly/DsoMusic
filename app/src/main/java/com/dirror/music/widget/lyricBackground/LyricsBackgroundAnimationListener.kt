package com.dirror.music.widget.lyricBackground

import android.view.animation.Animation
import com.dirror.music.widget.LyricsBackgroundLayerView

class LyricsBackgroundAnimationListener(private val lyricsBackgroundLayerView: LyricsBackgroundLayerView) :
    Animation.AnimationListener {
    override fun onAnimationEnd(animation: Animation) {
        lyricsBackgroundLayerView.m = null
    }

    override fun onAnimationRepeat(animation: Animation) {}
    override fun onAnimationStart(animation: Animation) {}
}