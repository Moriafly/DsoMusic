package com.dirror.music.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.Window

object StatusBarUtil {
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
}