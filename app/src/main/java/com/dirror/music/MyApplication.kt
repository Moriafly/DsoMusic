package com.dirror.music

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dirror.music.service.MusicBinderInterface
import com.dirror.music.service.MusicService
import okhttp3.Cookie

class MyApplication: Application() {
    companion object {
        lateinit var context: Context // 全局 context
        var musicBinderInterface: MusicBinderInterface? = null
        val musicConnection by lazy { MusicConnection() }

        val cookieStore: HashMap<String, List<Cookie>> = HashMap() // cookie

    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext // 全局 context

        startMusicService()
    }

    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        startService(intent) // 开启
        bindService(intent, musicConnection, BIND_AUTO_CREATE)
    }





}

class MusicConnection: ServiceConnection {
    // 连接
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

        MyApplication.musicBinderInterface = p1 as MusicBinderInterface
    }

    // 意外断开连接
    override fun onServiceDisconnected(p0: ComponentName?) {

    }

}