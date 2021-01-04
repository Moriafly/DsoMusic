package com.dirror.music.util

import android.os.CountDownTimer

/**
 * 定时器
 * 结束音乐播放
 */
class SleepTimer(millisInFuture: Long): CountDownTimer(1000L, 1000L) {

    override fun onTick(millisUntilFinished: Long) {
        TODO("Not yet implemented")
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }

}