package com.dirror.music.util

import androidx.viewpager2.widget.ViewPager2

/**
 * 拓展函数
 */

/**
 * 字节数组转 16 进制字符串
 */
fun ByteArray.toHex(): String? {
    val stringBuilder = StringBuilder("")
    if (this.isEmpty()) {
        return null
    }
    for (element in this) {
        val v = element.toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}

/**
 * 隐藏
 */
fun ViewPager2.hideScrollMode() {
    ViewPager2Util.changeToNeverMode(this)
}

