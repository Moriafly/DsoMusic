package com.dirror.music

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import cn.bmob.v3.Bmob
import com.dirror.music.service.MusicBinderInterface
import com.dirror.music.service.MusicService
import com.dirror.music.util.Secure
import com.dirror.music.util.toast
import okhttp3.Cookie

/**
 * 自定义 Application
 */
class MyApplication: Application() {
    companion object {
        lateinit var context: Context // 注入懒加载 全局 context
        var musicBinderInterface: MusicBinderInterface? = null
        val musicConnection by lazy { MusicConnection() }

        val cookieStore: HashMap<String, List<Cookie>> = HashMap() // cookie
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext // 全局 context

        if (Secure.isSecure()) {
            // 初始化 Bmob
            Bmob.initialize(this, "0d1d3b9214e037c76de958993ddd6563")
            // 开启服务
            startMusicService()
        } else {
            toast("检测到盗版 Dso Music")
            // 杀死自己
            Secure.killMyself()
        }
    }

    /**
     * 启动音乐服务
     */
    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        startService(intent) // 开启
        bindService(intent, musicConnection, BIND_AUTO_CREATE)
    }
}

class MusicConnection: ServiceConnection {
    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        MyApplication.musicBinderInterface = p1 as MusicBinderInterface
    }

    /**
     * 服务意外断开连接
     */
    override fun onServiceDisconnected(p0: ComponentName?) {

    }
}