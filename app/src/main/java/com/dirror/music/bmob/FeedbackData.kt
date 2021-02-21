package com.dirror.music.bmob

import android.os.Build
import androidx.annotation.Keep
import cn.bmob.v3.BmobObject
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.getVisionName

/**
 * @author 24568
 */
@Keep
class FeedbackData : BmobObject() {
    var feedback: String? = null
    var contact: String? = null
    val model: String = Build.MODEL // 设备名称
    val androidVersion: String = Build.VERSION.RELEASE // 安卓版本
    val appVer: String = "${getVisionName()}(${getVisionCode()})" // APP 版本
    val moreInfo: String = "" // 更多信息
}