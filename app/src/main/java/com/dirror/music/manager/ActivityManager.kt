package com.dirror.music.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dirror.music.R
import com.dirror.music.ui.activity.LoginActivity2

class ActivityManager: ActivityManagerInterface {

    override fun startLoginActivity(context: Context, activity: Activity) {
        val intent = Intent(context, LoginActivity2::class.java)
        activity.startActivityForResult(intent, 0)
        activity.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }

}