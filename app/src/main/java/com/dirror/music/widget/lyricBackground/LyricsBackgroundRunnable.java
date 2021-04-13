package com.dirror.music.widget.lyricBackground;

import com.dirror.music.widget.lyricBackground.LyricsBackgroundView;

/* compiled from: MusicApp */
public class LyricsBackgroundRunnable implements Runnable {
    public final /* synthetic */ LyricsBackgroundView f;

    public LyricsBackgroundRunnable(LyricsBackgroundView lyricsBackgroundLayerView) {
        this.f = lyricsBackgroundLayerView;
    }

    public void run() {
        LyricsBackgroundView lyricsBackgroundLayerView = this.f;
        lyricsBackgroundLayerView.setArtwork(lyricsBackgroundLayerView.currentBitmap);
        this.f.w = true;
    }
}