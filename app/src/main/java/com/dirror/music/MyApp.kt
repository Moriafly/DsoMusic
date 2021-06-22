/**
 * DsoMusic Copyright (C) 2020-2021 Moriafly
 *
 * This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 * This is free software, and you are welcome to redistribute it
 * under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 * You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <https://www.gnu.org/licenses/>.
 *
 * The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <https://www.gnu.org/licenses/why-not-lgpl.html>.
 */

package com.dirror.music

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDex
import com.dirror.music.manager.ActivityManager
import com.dirror.music.manager.CloudMusicManager
import com.dirror.music.manager.UserManager
import com.dirror.music.room.AppDatabase
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
class MyApp : Application() {

    companion object {

        init {
            System.loadLibrary("dso")
        }

        lateinit var mmkv: MMKV

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context // 注入懒加载 全局 context

        var musicController = MutableLiveData<MusicService.MusicController?>()

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

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        // 全局 context
        context = applicationContext
        // MMKV 初始化
        MMKV.initialize(context)
        mmkv = MMKV.defaultMMKV()
        // 管理初始化
        userManager = UserManager()
        activityManager = ActivityManager()
        cloudMusicManager = CloudMusicManager()
        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this)
        // 安全检查
        checkSecure()

        if (mmkv.decodeBool(Config.DARK_THEME, false)) {
            DarkThemeUtil.setDarkTheme(true)
        }

    }

    /**
     * 安全检查
     */
    private fun checkSecure() {
        if (Secure.isSecure()) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }

}