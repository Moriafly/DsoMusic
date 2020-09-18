package com.dirror.music.util

import android.app.Activity
import android.content.Intent
import android.media.audiofx.AudioEffect
import com.dirror.music.MyApplication
import com.dirror.music.MyApplication.Companion.context
import com.dirror.music.R

object IntentUtil {
    /**
     * 打开系统均衡器
     */
    fun openEqualizer(activity: Activity) {
        try {
            // 启动一个音频控制面板
            // 参考 https://www.cnblogs.com/dongweiq/p/7998445.html
            val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            // 传入 AudioSessionId
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MyApplication.musicBinderInterface?.getAudioSessionId())
            // 调用应用程序必须使用 startActivityForResult 方法启动控制面板，以便控制面板应用程序指示其包名称并用于跟踪此特定应用程序的更改
            activity.startActivityForResult(intent, 666)
            activity.overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        } catch (e: Exception) {
            toast("设备不支持均衡！")
        }
    }
}