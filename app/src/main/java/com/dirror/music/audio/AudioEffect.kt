package com.dirror.music.audio

import android.media.audiofx.NoiseSuppressor
import com.dirror.music.util.loge

object AudioEffect {
    /**
     * 开启或者关闭噪音压制控制器
     */
    fun noiseSuppressor(audioSession: Int, enabled: Boolean) {

        if (NoiseSuppressor.isAvailable()) {

            val noiseSuppressor = NoiseSuppressor.create(audioSession)
            noiseSuppressor.enabled = true
        } else {

        }
    }
}