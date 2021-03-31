package com.dirror.music.widget;

/* compiled from: MusicApp */
public class LyricsBackgroundRunnable implements Runnable {
    public final /* synthetic */ LyricsBackgroundLayerView f;

    public LyricsBackgroundRunnable(LyricsBackgroundLayerView lyricsBackgroundLayerView) {
        this.f = lyricsBackgroundLayerView;
    }

    public void run() {
        LyricsBackgroundLayerView lyricsBackgroundLayerView = this.f;
        lyricsBackgroundLayerView.setArtwork(lyricsBackgroundLayerView.bitmapG);
        this.f.w = true;
    }
}