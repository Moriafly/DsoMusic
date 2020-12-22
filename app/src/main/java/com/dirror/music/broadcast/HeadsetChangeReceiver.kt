package com.dirror.music.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.util.Config
import com.dirror.music.util.toast

/**
 * 耳机检查
 * 广播处理接口
 */
@Deprecated("过时")
class HeadsetChangeReceiver : BroadcastReceiver() {

    companion object {
        const val STATE = "state"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(STATE)) {
            if (intent.getIntExtra(STATE, 0) == 0) {
                // 断开耳机
                //可以加入自己想要处理的代码
                // Toast.makeText(context, DISCONTENT, Toast.LENGTH_SHORT).show()
                // 出现问题：关闭 Activity 也会调用这个
//                if (MyApplication.mmkv.decodeBool(Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET, true)) {
//                    MyApplication.musicBinderInterface?.pause()
//                    toast("来自耳机暂停")
//                }
            } else if (intent.getIntExtra(STATE, 0) == 1) {
                // 连接耳机
                // Toast.makeText(context, CONTENT, Toast.LENGTH_SHORT).show()
            }
        }
    }

}