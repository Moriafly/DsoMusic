package com.dirror.music.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.dirror.music.BuildConfig
import com.dirror.music.MyApplication
import com.dirror.music.cloudmusic.ArtistData
import com.dirror.music.music.StandardArtistData


/**
 * 顶层函数类
 */

/**
 * 设置状态栏图标颜色
 * @param dark true 为黑色，false 为白色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

/**
 * 全局 toast
 */
fun toast(msg: String) {
    runOnMainThread {
        Toast.makeText(MyApplication.context, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 * 全局 log
 */
fun loge(msg: String) {
    runOnMainThread {
        Log.e("Dirror 音乐", "【$msg】")
    }
}

// 运行在主线程，更新 UI
fun runOnMainThread(runnable: Runnable) {
    Handler(Looper.getMainLooper()).post(runnable)
}

// dp 转 px
fun dp2px(dp: Float): Float = dp * MyApplication.context.resources.displayMetrics.density

// http 转 https
fun http2https(http: String): String {
    return http.replace("http", "https")
}

/**
 * 获取系统当前时间
 */
fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

/**
 * 标准歌手数组转文本
 * @param artistList 歌手数组
 * @return 文本
 */
fun parseArtist(artistList: ArrayList<StandardArtistData>): String {
    var artist = ""
    for (artistName in 0..artistList.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += artistList[artistName].name
    }
    return artist
}

/**
 * 通过浏览器打开网页
 * @param context
 * @url 网址
 */
fun openUrlByBrowser(context: Context, url: String) {
    val intent = Intent()
    intent.action = "android.intent.action.VIEW"
    val contentUrl = Uri.parse(url)
    intent.data = contentUrl
    startActivity(context, intent, Bundle())
}

// 毫秒转日期
fun msTimeToFormatDate(msTime: Long): String {
    return TimeUtil.msTimeToFormatDate(msTime)
}

/**
 * 获取状态栏高度
 * @return px 值
 */
fun getStatusBarHeight(window: Window, context: Context): Int {
    return StatusBarUtil.getStatusBarHeight(window, context)
}

fun getNavigationBarHeight(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        val resources: Resources = activity.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        //超出系统默认的导航栏高度以上，则认为存在虚拟导航
        if (realSize.y - size.y > height - 10) {
            height
        } else 0
    } else {
        val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
        val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (menu || back) {
            0
        } else {
            val resources: Resources = activity.resources
            val resourceId: Int =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        }
    }
}

// 获取版本号
fun getVisionCode(): Int {
    return BuildConfig.VERSION_CODE
}

fun getVisionName(): String {
    return BuildConfig.VERSION_NAME
}