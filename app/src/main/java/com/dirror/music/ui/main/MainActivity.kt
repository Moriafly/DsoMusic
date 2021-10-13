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

package com.dirror.music.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.databinding.ActivityMainBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.activity.AboutActivity
import com.dirror.music.ui.activity.FeedbackActivity
import com.dirror.music.ui.activity.SearchActivity
import com.dirror.music.ui.activity.SettingsActivity
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.main.viewmodel.MainViewModel
import com.dirror.music.util.*
import com.dirror.music.util.cache.ACache
import com.dirror.music.util.dp
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlin.concurrent.thread

/**
 * MainActivity
 */
class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_LOGIN = "com.dirror.music.LOGIN"
    }

    private lateinit var binding: ActivityMainBinding

    /* 登录广播接受 */
    private lateinit var loginReceiver: LoginReceiver

    /* 设置改变广播接收 */
    private lateinit var settingsChangeReceiver: SettingsChangeReceiver

    private val mainViewModel: MainViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            mainViewModel.statusBarHeight.value = insets.systemWindowInsetTop
            mainViewModel.navigationBarHeight.value = insets.systemWindowInsetBottom
            insets
        }
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        // Intent 过滤器
        var intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_LOGIN)
        loginReceiver = LoginReceiver()
        registerReceiver(loginReceiver, intentFilter)

        intentFilter = IntentFilter()
        intentFilter.addAction(SettingsActivity.ACTION)
        settingsChangeReceiver = SettingsChangeReceiver()
        registerReceiver(settingsChangeReceiver, intentFilter)

        // 检查新版本
        UpdateUtil.checkNewVersion(this, false)
    }

    override fun initView() {
        thread {
            ACache.get(this).getAsBitmap(Config.APP_THEME_BACKGROUND)?.let {
                runOnMainThread {
                    binding.ivTheme.setImageBitmap(it)
                }
            }
        }

        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurViewPlay.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MyFragment()
                    else -> HomeFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my)
                else -> getString(R.string.find)
            }
        }.attach()

        val select = App.mmkv.decodeInt(Config.SELECT_FRAGMENT, 0)
        binding.viewPager2.setCurrentItem(select, false)

        ViewPager2Util.changeToNeverMode(binding.viewPager2)
    }

    override fun initListener() {
        with(binding) {
            // 搜索按钮
            ivSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                overridePendingTransition(
                    R.anim.anim_alpha_enter,
                    R.anim.anim_no_anim
                )
            }
            // 设置按钮
            ivSettings.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    App.mmkv.encode(Config.SELECT_FRAGMENT, position)
                }
            })
            // 侧滑
            with(menuMain) {
                itemSponsor.setOnClickListener {
                    App.activityManager.startWebActivity(this@MainActivity, AboutActivity.SPONSOR)
                }
                itemSwitchAccount.setOnClickListener {
                    App.activityManager.startLoginActivity(this@MainActivity)
                }
                itemSettings.setOnClickListener {
                    App.activityManager.startSettingsActivity(this@MainActivity)
                }
                // 反馈
                itemFeedback.setOnClickListener {
                    startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                }
                itemAbout.setOnClickListener {
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                }
                itemExitApp.setOnClickListener {
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

    override fun initObserver() {
        mainViewModel.statusBarHeight.observe(this, {
            (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = 56.dp() + it
            }
            (binding.viewPager2.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = 56.dp() + it
            }
            (binding.menuMain.llMenu.layoutParams as FrameLayout.LayoutParams).apply {
                topMargin = it + 8.dp()
            }
        })
        mainViewModel.navigationBarHeight.observe(this, {
            binding.miniPlayer.root.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = it
            }
            binding.blurViewPlay.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = 52.dp() + it
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(loginReceiver)
        unregisterReceiver(settingsChangeReceiver)
    }

    inner class LoginReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 通知 viewModel
            mainViewModel.setUserId()
        }
    }

    inner class SettingsChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mainViewModel.updateUI()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

    private var startX = 0
    private var startY = 0
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = kotlin.math.abs(endX - startX)
                val disY = kotlin.math.abs(endY - startY)
                if (disX < disY) {
                    // 禁止 ViewPager2
                    binding.viewPager2.isUserInputEnabled = false
                }
            }
            MotionEvent.ACTION_UP -> {
                startX = 0
                startY = 0
                // 恢复
                binding.viewPager2.isUserInputEnabled = true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}