package com.dirror.music.ui.main

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.activity.AboutActivity
import com.dirror.music.ui.activity.FeedbackActivity
import com.dirror.music.ui.compose.RoundedColumn
import com.dirror.music.ui.main.viewmodel.MainViewModel
import com.dirror.music.util.Secure
import com.dirror.music.util.compose.textDp

@Composable
fun MainMenu(activity: Activity, mainViewModel: MainViewModel) {
    val statusBarHeight = mainViewModel.statusBarHeight.observeAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorResource(id = R.color.dso_color_page))
            .padding(top = px2dp((statusBarHeight ?: 0).toFloat()).dp)
    ) {
        RoundedColumn {
            MenuItem(resId = R.drawable.ic_settings, title = "设置") {
                App.activityManager.startSettingsActivity(activity)
            }
            MenuItem(resId = R.drawable.ic_feedback, title = "反馈") {
                activity.startActivity(Intent(activity, FeedbackActivity::class.java))
            }
            MenuItem(resId = R.drawable.ic_sponsor, title = "赞赏") {
                App.activityManager.startWebActivity(activity, AboutActivity.SPONSOR)
            }
            // MenuItem(resId = R.drawable.ic_extension, title = "拓展")
            MenuItem(resId = R.drawable.ic_about, title = "关于") {
                activity.startActivity(Intent(activity, AboutActivity::class.java))
            }
            MenuItem(resId = R.drawable.ic_we, title = "切换账号") {
                App.activityManager.startLoginActivity(activity)
            }
            MenuItem(resId = R.drawable.ic_exit_app, title = "退出应用") {
                App.musicController.value?.stopMusicService()
                ActivityCollector.finishAll()
                object : Thread() {
                    override fun run() {
                        super.run()
                        sleep(500)
                        Secure.killMyself()
                    }
                }.start()
            }
        }
    }
}

@Composable
private fun MenuItem(
    @DrawableRes resId: Int,
    title: String,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                onClick?.invoke()
            }
    ) {
        Icon(
            modifier = Modifier
                .size(56.dp)
                .padding(16.dp),
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = colorResource(id = R.color.colorIconForeground)
        )
        Text(
            modifier = Modifier
                .padding(56.dp, 0.dp)
                .align(Alignment.CenterStart),
            text = title,
            fontSize = 16.textDp
        )
    }
}

private fun px2dp(px: Float): Float = px / App.context.resources.displayMetrics.density
