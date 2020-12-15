package com.dirror.music.manager

import android.app.Activity
import android.content.Intent
import com.dirror.music.R
import com.dirror.music.ui.activity.*

class ActivityManager: ActivityManagerInterface {

    override fun startLoginActivity(activity: Activity) {
        val intent = Intent(activity, LoginActivity2::class.java)
        activity.startActivityForResult(intent, 0)
        activity.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }

    override fun startFeedbackActivity(activity: Activity) {
        val intent = Intent(activity, FeedbackActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startWebActivity(activity: Activity, url: String) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra("extra_webUrlStr", url)
        activity.startActivity(intent)
    }

    /**
     * 启动评论 activity
     */
    override fun startCommentActivity(activity: Activity, source: Int, id: String) {
        val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra(CommentActivity.EXTRA_INT_SOURCE, source)
        intent.putExtra(CommentActivity.EXTRA_STRING_ID, id)
        activity.startActivity(intent)
        activity.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }

    override fun startUserActivity(activity: Activity, userId: Long) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra(UserActivity.EXTRA_LONG_USER_ID, userId)
        activity.startActivity(intent)
    }

    override fun startLoginByPhoneActivity(activity: Activity) {
        val intent = Intent(activity, LoginByPhoneActivity::class.java)
        activity.startActivityForResult(intent, 0)
//        activity.overridePendingTransition(
//            R.anim.anim_slide_enter_bottom,
//            R.anim.anim_no_anim
//        )
    }

    override fun startSettingsActivity(activity: Activity) {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startPrivateLetterActivity(activity: Activity) {
        val intent = Intent(activity, PrivateLetterActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startPlayerActivity(activity: Activity) {
        val intent = Intent(activity, PlayerActivity::class.java)
        activity.startActivity(intent)
    }

}