package com.dirror.music

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.Bmob
import com.dirror.music.manager.ActivityManager
import com.dirror.music.manager.CloudMusicManager
import com.dirror.music.manager.UserManager
import com.dirror.music.room.AppDatabase
import com.dirror.music.service.MusicControllerInterface
import com.dirror.music.service.MusicService
import com.dirror.music.service.MusicServiceConnection
import com.dirror.music.util.*
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

/**
 * 自定义 Application
 * @author Moriafly
 */
@Keep
class MyApplication : Application() {

    companion object {
        // 加载本地库
        init {
            System.loadLibrary("dso")
        }

        lateinit var config: Config

        lateinit var context: Context // 注入懒加载 全局 context
        var musicController = MutableLiveData<MusicService.MusicController?>().also {
            it.value = null
        }
        val musicServiceConnection by lazy { MusicServiceConnection() } // 音乐服务连接
        // 管理
        lateinit var userManager: UserManager
        lateinit var activityManager: ActivityManager
        lateinit var cloudMusicManager: CloudMusicManager

        // 数据库
        lateinit var appDatabase: AppDatabase
    }



    /* 获取 Bmob */
    private external fun getBmobAppKey(): String

    /* 获取友盟 */
    private external fun getUmAppKey(): String

    override fun onCreate() {
        super.onCreate()
        // 全局 context
        context = applicationContext
        // MMKV 初始化
        MMKV.initialize(this)
        config = Config()
        // 管理初始化
        userManager = UserManager()
        activityManager = ActivityManager()
        cloudMusicManager = CloudMusicManager()
        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this)
        // 安全检查
        checkSecure()

        if (config.mmkv.decodeBool(Config.DARK_THEME, false)) {
            DarkThemeUtil.setDarkTheme(true)
        }

    }

    /**
     * 安全检查
     */
    private fun checkSecure() {
        if (Secure.isSecure()) {
            // 初始化 Bmob
            Bmob.initialize(this, getBmobAppKey())
            // 初始化友盟
            UMConfigure.init(context, getUmAppKey(), "", UMConfigure.DEVICE_TYPE_PHONE, "")
            // 选用 AUTO 页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
            // 开启音乐服务
            startMusicService()
        } else {
            Secure.killMyself()
        }
    }

    /**
     * 启动音乐服务
     */
    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onTerminate() {
        super.onTerminate()
        toast("回收")
    }

}