package com.dirror.music.audio

import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import com.dirror.music.util.loge
import org.jetbrains.annotations.TestOnly

/**
 * 音频效果
 */
object AudioEffect {

    init {

    }

    /**
     * 开启或者关闭噪音压制控制器
     */
    @TestOnly
    fun noiseSuppressor(audioSession: Int, enabled: Boolean) {

        if (NoiseSuppressor.isAvailable()) {

            val noiseSuppressor = NoiseSuppressor.create(audioSession).also {
                it.enabled = enabled
            }
        } else {

        }
    }

    /**
     * 自动增益功能
     */
    @TestOnly
    fun automaticGainControl(audioSession: Int, enabled: Boolean) {
        val automaticGainControl = AutomaticGainControl.create(audioSession)
        if (AutomaticGainControl.isAvailable()) {
            automaticGainControl.enabled = enabled
        }
    }

}