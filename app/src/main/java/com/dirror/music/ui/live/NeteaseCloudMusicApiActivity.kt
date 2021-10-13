package com.dirror.music.ui.live

import com.dirror.music.App
import com.dirror.music.databinding.ActivityNeteaseCloudMusicApiBinding
import com.dirror.music.ui.activity.SettingsActivity
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.BroadcastUtil
import com.dirror.music.util.Config
import com.dirror.music.util.openUrlByBrowser

class NeteaseCloudMusicApiActivity : BaseActivity() {

    companion object {
        private const val URL_NETEASE_CLOUD_MUSIC_API = "https://github.com/Binaryify/NeteaseCloudMusicApi"
    }

    private lateinit var binding: ActivityNeteaseCloudMusicApiBinding

    override fun initBinding() {
        binding = ActivityNeteaseCloudMusicApiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            switcherEnableService.setChecked(App.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false))
            etService.setText(App.mmkv.decodeString(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, ""))
        }
    }

    override fun initListener() {
        with(binding) {
            switcherEnableService.setOnCheckedChangeListener {
                App.mmkv.encode(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, it)
            }
            itemNeteaseCloudMusicApiGithub.setOnClickListener {
                openUrlByBrowser(this@NeteaseCloudMusicApiActivity, URL_NETEASE_CLOUD_MUSIC_API)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        App.mmkv.encode(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, binding.etService.text.toString())
        BroadcastUtil.send(this, SettingsActivity.ACTION)
    }

}