package com.dirror.music.service.player

import android.media.MediaPlayer

/**
 * DsoPlayer
 */
class DsoPlayer: MediaPlayer() {

    /**
     * 销毁 DsoPlayer
     */
    fun destroy() {
        reset()
        release()
    }

}