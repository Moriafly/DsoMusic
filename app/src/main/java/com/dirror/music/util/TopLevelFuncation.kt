package com.dirror.music.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.Toast
import com.dirror.music.MyApplication
import com.dirror.music.cloudmusic.ArtistData

/**
 * 顶层函数类
 */
val CLOUD_MUSIC_API = "https://musicapi.leanapp.cn"
/**
 * 设置状态栏图标颜色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

fun toast(msg: String) {
    runOnMainThread {
        Toast.makeText(MyApplication.context, msg, Toast.LENGTH_SHORT).show()
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

fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

fun parseArtist(artistList: List<ArtistData>): String {
    var artist = ""
    for (artistName in 0..artistList.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += artistList[artistName].name
    }
    return artist
}

// 获取状态栏高度
@SuppressLint("PrivateApi")
fun getStatusBarHeight(window: Window, context: Context): Int {
    val localRect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(localRect)
    var mStatusBarHeight = localRect.top
    if (0 == mStatusBarHeight) {
        try {
            val localClass = Class.forName("com.android.internal.R\$dimen")
            val localObject = localClass.newInstance()
            val i5 =
                localClass.getField("status_bar_height")[localObject].toString().toInt()
            mStatusBarHeight = context.resources.getDimensionPixelSize(i5)
        } catch (var6: ClassNotFoundException) {
            var6.printStackTrace()
        } catch (var7: IllegalAccessException) {
            var7.printStackTrace()
        } catch (var8: InstantiationException) {
            var8.printStackTrace()
        } catch (var9: NumberFormatException) {
            var9.printStackTrace()
        } catch (var10: IllegalArgumentException) {
            var10.printStackTrace()
        } catch (var11: SecurityException) {
            var11.printStackTrace()
        } catch (var12: NoSuchFieldException) {
            var12.printStackTrace()
        }
    }
    if (0 == mStatusBarHeight) {
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            mStatusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
    }
    return mStatusBarHeight
}