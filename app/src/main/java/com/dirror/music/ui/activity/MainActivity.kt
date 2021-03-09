package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import coil.size.ViewSizeResolver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.broadcast.HeadsetChangeReceiver
import com.dirror.music.databinding.ActivityMainBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.viewmodel.MainViewModel
import com.dirror.music.util.*
import com.dirror.music.util.cache.ACache
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlin.concurrent.thread

/**
 * MainActivity
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headSetChangeReceiver: HeadsetChangeReceiver // 耳机广播接收
    private lateinit var loginReceiver: LoginReceiver // 登录广播接收

    // 不要写成 mainViewModel = MainViewModel()
    private val mainViewModel: MainViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        // Intent 过滤器
        var intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.HEADSET_PLUG")
        headSetChangeReceiver = HeadsetChangeReceiver()
        registerReceiver(headSetChangeReceiver, intentFilter) // 注册接收器
        intentFilter = IntentFilter()

        intentFilter.addAction("com.dirror.music.LOGIN")
        loginReceiver = LoginReceiver()
        registerReceiver(loginReceiver, intentFilter) // 注册接收器
        // 检查新版本
        UpdateUtil.checkNewVersion(this, false)
    }

    override fun initView() {
        thread {
            ACache.get(this).getAsBitmap(Config.APP_THEME_BACKGROUND)?.let {
                runOnMainThread {
                    binding.ivTheme.setImageBitmap(it)
                    val pixelsPair = ScreenUtil.getDisplayPixels()
                    Glide.with(this)
                        .asBitmap()
                        .load(it)
                        .override(pixelsPair.first, pixelsPair.second)
                        .apply(RequestOptions().centerCrop())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                // binding.navigationView.background = resource.toDrawable(resources)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })

                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.allowEnterTransitionOverlap = true
            window.allowReturnTransitionOverlap = true
        }


        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurViewTop.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        binding.blurViewPlay.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        // 适配状态栏
        val statusBarHeight = getStatusBarHeight(window, this) // px
        mainViewModel.statusBarHeight.value = statusBarHeight
        (binding.blurViewTop.layoutParams as ConstraintLayout.LayoutParams).apply {
            height = 56.dp() + statusBarHeight
        }
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply{
            topMargin = statusBarHeight
        }
        // 侧滑状态栏适配
        (binding.menuMain.cvOthers.layoutParams as LinearLayout.LayoutParams).apply{
            topMargin = statusBarHeight + 8.dp()
        }

        // 适配导航栏
        val navigationBarHeight = if (MyApplication.mmkv.decodeBool(Config.PARSE_NAVIGATION, true)) {
            getNavigationBarHeight(this)
        } else {
            0
        }

        (binding.miniPlayer.root.layoutParams as ConstraintLayout.LayoutParams).apply{
            bottomMargin = navigationBarHeight
        }
        (binding.blurViewPlay.layoutParams as ConstraintLayout.LayoutParams).apply{
            height = 52.dp() + navigationBarHeight
        }

        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2 // 2 个页面
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> mainViewModel.myFragment
                    else -> mainViewModel.homeFragment
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my)
                else -> getString(R.string.home)
            }
        }.attach()

        ViewPager2Util.changeToNeverMode(binding.viewPager2)

    }

    override fun initListener() {
        //
        binding.apply {
            // 搜索按钮
            ivSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
            // 设置按钮
            ivSettings.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        // 侧滑
        binding.menuMain.apply {
            itemSponsor.setOnClickListener {
                startActivity(Intent(this@MainActivity, SponsorActivity::class.java))
            }
            itemSwitchAccount.setOnClickListener {
                MyApplication.activityManager.startLoginActivity(this@MainActivity)
            }
            itemSettings.setOnClickListener {
                MyApplication.activityManager.startSettingsActivity(this@MainActivity)
            }
            itemPrivateLetter.setOnClickListener {
                val cookie = MyApplication.userManager.getCloudMusicCookie()
                if (cookie != "") {
                    MyApplication.activityManager.startPrivateLetterActivity(this@MainActivity)
                } else {
                    toast("当前为离线模式，请登录")
                }
            }
            // 反馈
            itemFeedback.setOnClickListener {
                startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
            }
            itemExitApp.setOnClickListener {
                MyApplication.musicController.value?.stopMusicService()
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

        binding.menuMain.itemAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }


    }

    override fun initObserver() {

    }

    override fun onStart() {
        super.onStart()
        // 请求广播
        MyApplication.musicController.value?.sendBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(headSetChangeReceiver)
        unregisterReceiver(loginReceiver)
    }

    inner class LoginReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 通知 viewModel
            mainViewModel.setUserId()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

}