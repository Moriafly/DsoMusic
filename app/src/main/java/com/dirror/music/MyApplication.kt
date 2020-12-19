package com.dirror.music

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import cn.bmob.v3.Bmob
import com.dirror.music.manager.ActivityManager
import com.dirror.music.manager.CloudMusicManager
import com.dirror.music.manager.UserManager
import com.dirror.music.service.MusicBinderInterface
import com.dirror.music.service.MusicService
import com.dirror.music.util.Secure
import com.dirror.music.util.toast
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import okhttp3.Cookie
import org.jetbrains.annotations.TestOnly


/**
 * 自定义 Application
 * @author Moriafly
 */
class MyApplication : Application() {

    companion object {
        const val BMOB_APP_KEY = "0d1d3b9214e037c76de958993ddd6563" // Bmob App Key
        const val UM_APP_KEY = "5fb38e09257f6b73c0961382" // 友盟 SDK APP KEY

        lateinit var context: Context // 注入懒加载 全局 context
        lateinit var mmkv: MMKV // mmkv
        var musicBinderInterface: MusicBinderInterface? = null // MusicBinderInterface
        val musicServiceConnection by lazy { MusicServiceConnection() } // 音乐服务连接

        val cookieStore: HashMap<String, List<Cookie>> = HashMap() // cookie

        // 管理
        lateinit var userManager: UserManager
        lateinit var activityManager: ActivityManager
        lateinit var cloudMusicManager: CloudMusicManager

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext // 全局 context

        MMKV.initialize(this)
        mmkv = MMKV.defaultMMKV() // MMKV

        userManager = UserManager()
        activityManager = ActivityManager()
        cloudMusicManager = CloudMusicManager()

        checkSecure()
    }


    /**
     * 安全检查
     * 自己签名请去除
     */
    private fun checkSecure() {
        if (Secure.isSecure()) {
            // 初始化 Bmob
            Bmob.initialize(this, BMOB_APP_KEY)
            // 初始化友盟
            UMConfigure.init(context, UM_APP_KEY, "", UMConfigure.DEVICE_TYPE_PHONE, "")
            // 选用 AUTO 页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
            // 开启音乐服务
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
    @TestOnly
    fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        // 安卓 8.0 后开启前台服务，要在短时间内响应，现在不知道怎么搞
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }

}

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

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
        // MyApplication.musicBinderInterface = null
    }

}