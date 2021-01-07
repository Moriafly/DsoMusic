package com.dirror.music.bmob

import android.os.Build
import androidx.annotation.Keep
import cn.bmob.v3.BmobObject

/**
 * @author 24568
 */
@Keep
class FeedbackData : BmobObject() {
    var feedback: String? = null
    var contact: String? = null
    val model: String = Build.MODEL // 设备名称
    val androidVersion: String = Build.VERSION.RELEASE // 安卓版本
}