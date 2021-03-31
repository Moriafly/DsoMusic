package com.dirror.music.widget;

import android.view.animation.Animation;

/* compiled from: MusicApp */
public class LyricsBackgroundAnimationListener implements Animation.AnimationListener {
    public final /* synthetic */ LyricsBackgroundLayerView lyricsBackgroundLayerView;

    public LyricsBackgroundAnimationListener(LyricsBackgroundLayerView lyricsBackgroundLayerView) {
        this.lyricsBackgroundLayerView = lyricsBackgroundLayerView;
    }

    public void onAnimationEnd(Animation animation) {
        this.lyricsBackgroundLayerView.m = null;
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }
}