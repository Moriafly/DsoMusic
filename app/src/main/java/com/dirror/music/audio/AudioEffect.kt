package com.dirror.music.audio

import android.media.audiofx.NoiseSuppressor

object AudioEffect {
    /**
     * 开启或者关闭噪音压制控制器
     */
    fun noiseSuppressor(audioSession: Int, enabled: Boolean) {
        val noiseSuppressor = NoiseSuppressor.create(audioSession)
        noiseSuppressor.enabled = enabled
    }
}