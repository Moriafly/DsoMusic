package com.dirror.music.ui.dialog

import android.app.Activity
import android.content.Context
import com.dirror.music.MyApplication
import com.dirror.music.databinding.DialogSoundEffectBinding
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.IntentUtil

class SoundEffectDialog(context: Context, private val activity: Activity): BaseBottomSheetDialog(context) {

    private val binding = DialogSoundEffectBinding.inflate(layoutInflater)

    private var speed = 1f

    init {
        setContentView(binding.root)
    }

    override fun initView() {
        speed = MyApplication.musicBinderInterface?.getSpeed() ?: 1f
        refreshPitch()
    }

    override fun initListener() {
        binding.apply {
            itemEqualizer.setOnClickListener {
                IntentUtil.openEqualizer(activity)
                dismiss()
            }
            ivIncreasePitch.setOnClickListener {
                MyApplication.musicBinderInterface?.increasePitchLevel()
                refreshPitch()
            }
            ivDecreasePitch.setOnClickListener {
                MyApplication.musicBinderInterface?.decreasePitchLevel()
                refreshPitch()
            }
        }
    }

    /**
     * 刷新 Pitch
     */
    private fun refreshPitch() {
        binding.tvPitch.text = MyApplication.musicBinderInterface?.getPitchLevel().toString()
    }

}