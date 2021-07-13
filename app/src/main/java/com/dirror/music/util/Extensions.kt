package com.dirror.music.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dirror.music.music.standard.data.StandardSongData

/**
 * 拓展函数
 */

/**
 * 隐藏
 */
fun ViewPager2.hideScrollMode() {
    ViewPager2Util.changeToNeverMode(this)
}

/**
 * dp
 */
fun Int.dp(): Int {
    return dp2px(this.toFloat()).toInt()
}

/**
 * 判断是否是中文字符
 */
fun Char.isChinese(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)
    if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    ) // 中日韩象形文字
    {
        return true
    }
    return false
}

fun Int.colorAlpha(alpha: Float): Int {
    val a = if (alpha in 0f..1f) {
        Color.alpha(this) * alpha
    } else {
        255
    }.toInt()
    return Color.argb(a, Color.red(this), Color.green(this), Color.blue(this))
}

/**
 * 标准歌手数组转文本
 * @return 文本
 */
fun ArrayList<StandardSongData.StandardArtistData>.parse(): String {
    var artist = ""
    for (artistName in 0..this.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += this[artistName].name
    }
    return artist
}

fun Int.asColor(context: Context) = ContextCompat.getColor(context, this)

fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context, this)

val String.Companion.EMPTY
    get() = ""


