package com.dirror.music.service.test

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * MiniPlayer
 * View 跨 Activity 复用方案
 * 是指手动使用 ApplicationContext 创建需要被复用的 View，
 * 并且使用单例 Manager 持有该 View，
 * 添加删除可复用 View 可以统一在 Activity 生命周期函数中实现，
 * 示例代码如下：
 * ------
 * 作者：网易云音乐大前端团队
 * 链接：https://juejin.cn/post/6904083382620291085
 * 来源：掘金
 * 理论上这是一种更加灵活的方案，使用 Application 作为 View 的 Context 也不用担心泄漏问题，
 * 不过由于在这次小窗的需求中涉及到老的页面和新页面的播放器复用，
 * 在很多场景下并不是一个统一的播放View，所以没有采用这种方案，
 * 不过这个方案在网易云音乐的音街 App 的 mini 播放条上已经被使用，
 * 有兴趣的小伙伴也可以尝试下。
 */
object MiniPlayerManager: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(activity: Activity) {
        // ...
        // removePlayerBarFromWindow(activity)
        // addPlayerBarToWindow(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        // ...
//        if (activity.isFinishing && getMiniPlayerBarParentContext() == activity) {
//            // removePlayerBarFromWindow(activity, true)
//        }
    }

    override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
        TODO("Not yet implemented")
    }

//    private fun getPlayerBar(activityBase: Activity): MiniPlayerBar {
//        synchronized(this) {
//            if (miniPlayerBar == null) {
//                miniPlayerBar = MiniPlayerBar(activityBase.applicationContext)
//            }
//            ...
//            return miniPlayerBar!!
//        }
//    }

}