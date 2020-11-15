package com.dirror.music.ui.activity

import android.content.Intent
import android.view.View
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(R.layout.activity_settings) {

    override fun initData() {

    }

    override fun initView() {
        switchPlayOnMobile.isChecked = StorageUtil.getBoolean(StorageUtil.PLAY_ON_MOBILE, false)
        switchPauseSongAfterUnplugHeadset.isChecked = MyApplication.mmkv.decodeBool(Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET, true)
        itemFoyouVersion.setValue(FoyouLibrary.VERSION)
    }

    override fun initListener() {
        // 反馈
        itemFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        itemSourceCode.setOnClickListener {
            openUrlByBrowser(this, "https://github.com/Moriafly/dirror-music")
        }

        itemPlayOnMobile.setOnClickListener {
            switchPlayOnMobile.isChecked = !switchPlayOnMobile.isChecked
        }

        switchPlayOnMobile.setOnCheckedChangeListener { buttonView, isChecked ->
            StorageUtil.putBoolean(StorageUtil.PLAY_ON_MOBILE, isChecked)
        }

        itemPauseSongAfterUnplugHeadset.setOnClickListener {
            switchPauseSongAfterUnplugHeadset.isChecked = !switchPauseSongAfterUnplugHeadset.isChecked
        }

        switchPauseSongAfterUnplugHeadset.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.mmkv.encode(Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET, isChecked)
        }

        itemAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_slide_enter_right,
            R.anim.anim_slide_exit_left
        )
    }
}