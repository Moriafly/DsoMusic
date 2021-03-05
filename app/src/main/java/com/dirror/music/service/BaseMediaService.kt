package com.dirror.music.service

import android.media.MediaPlayer
import androidx.media.MediaBrowserServiceCompat

/**
 * 抽象类
 */
abstract class BaseMediaService: MediaBrowserServiceCompat() {

    /**
     * MediaPlayer
     */
    var mediaPlayer: MediaPlayer? = null

    companion object {


    }

}