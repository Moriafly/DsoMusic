package com.dirror.music.ui.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import com.dirror.music.App
import com.dirror.music.databinding.DialogSoundEffectBinding
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.toast

class SoundEffectDialog(context: Context, private val activity: Activity): BaseBottomSheetDialog(context) {

    private val binding = DialogSoundEffectBinding.inflate(layoutInflater)

    private var speed = 1f

    init {
        setContentView(binding.root)
    }

    override fun initView() {
        speed = App.musicController.value?.getSpeed() ?: 1f
        refreshPitch()
    }

    override fun initListener() {
        binding.apply {
            itemEqualizer.setOnClickListener {
                try {
                    // 启动一个音频控制面板
                    // 参考 https://www.cnblogs.com/dongweiq/p/7998445.html
                    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    // 传入 AudioSessionId
                    intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, App.musicController.value?.getAudioSessionId())
                    // 调用应用程序必须使用 startActivityForResult 方法启动控制面板，以便控制面板应用程序指示其包名称并用于跟踪此特定应用程序的更改
                    activity.startActivityForResult(intent, 666)
                } catch (e: Exception) {
                    toast("设备不支持均衡！")
                }
                dismiss()
            }
            ivIncreasePitch.setOnClickListener {
                App.musicController.value?.increasePitchLevel()
                refreshPitch()
            }
            ivDecreasePitch.setOnClickListener {
                App.musicController.value?.decreasePitchLevel()
                refreshPitch()
            }
        }
    }

    /**
     * 刷新 Pitch
     */
    private fun refreshPitch() {
        binding.tvPitch.text = App.musicController.value?.getPitchLevel().toString()
    }

}