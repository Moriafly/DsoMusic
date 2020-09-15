package com.dirror.music.util

object TimeUtil {
    private const val HOUR = 60 * 60 * 1000
    private const val MIN = 60 * 1000
    private const val SEC = 1000

    fun parseDuration(time: Int): String {
        // 时，分，秒
        val hour = time / HOUR
        val min = time % HOUR / MIN
        val sec = time % MIN / SEC
        return if (hour == 0) {
            String.format("%02d:%02d", min, sec)
        } else {
            String.format("%02d:%02d:%02d", hour, min, sec)
        }
    }
}