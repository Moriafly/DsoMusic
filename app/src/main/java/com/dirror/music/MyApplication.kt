package com.dirror.music

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dirror.music.service.MusicBinderInterface
import com.dirror.music.service.MusicService
import com.dirror.music.util.Encrypt
import com.dirror.music.util.Secure
import com.dirror.music.util.loge
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
        if (isSecure()) {
            toast("Dso Music")
            startMusicService()
        } else {
            toast("检测到盗版 Dso Music")
            // 杀死自己
            android.os.Process.killProcess(android.os.Process.myPid())
        }

    }

    /**
     * 检查应用安全
     * @return true 通过检测
     */
    private fun isSecure(): Boolean {
        // 判断当前应用是否处于 debug 状态，若处于直接 true
        if (Secure.isDebug()) {
            toast("DEBUG 状态")
            return true
        }

        val signature = Secure.getSignature() // 获取当前应用签名哈希值
        if (signature != Secure.SIGNATURE) {
            return false
        }
        return true
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