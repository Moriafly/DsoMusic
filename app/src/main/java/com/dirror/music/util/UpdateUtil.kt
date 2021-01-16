package com.dirror.music.util

import android.app.Activity
import androidx.annotation.Keep
import com.dirror.music.ui.dialog.UpdateDialog
import com.google.gson.Gson
import java.lang.Exception

@Keep
object UpdateUtil {

    /**
     * 检查新版本
     * 传入 [activity]，[showLastedToast] 开启表示显示如果是最新版或是获取数据错误时弹出 Toast
     */
    fun checkNewVersion(activity: Activity, showLastedToast: Boolean) {
        getServerVersion({ updateData ->
            runOnMainThread {
                if (updateData.code > getVisionCode()) {
                    // 有新版
                    UpdateDialog(activity, updateData).show()
                } else {
                    if (showLastedToast) {
                        toast("已是最新版本\n服务器版本：${updateData.code}")
                    }
                }
            }
        }, {
            if (showLastedToast) {
                toast("获取服务器版本信息失败")
            }
        })
    }

    /**
     * 检查服务器版本
     */
    private fun getServerVersion(success: (UpdateData) -> Unit, failure: () -> Unit) {
        val url = "https://moriafly.xyz/dirror-music/update.json"
        MagicHttp.OkHttpManager().newGet(url, {
            // 成功
            try {
                success.invoke(Gson().fromJson(it, UpdateData::class.java))
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {

        })
    }

    @Keep
    data class UpdateData(
        val name: String,
        val code: Int,
        val content: String,
        val url: String, // 下载链接
    )

}