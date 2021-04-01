package com.dirror.music.widget.lyricBackground

import android.view.animation.Animation

class LyricsBackgroundAnimationListener(private val lyricsBackgroundLayerView: LyricsBackgroundView) :
    Animation.AnimationListener {
    override fun onAnimationEnd(animation: Animation) {
        lyricsBackgroundLayerView.m = null
    }

    override fun onAnimationRepeat(animation: Animation) {}
    override fun onAnimationStart(animation: Animation) {}
}