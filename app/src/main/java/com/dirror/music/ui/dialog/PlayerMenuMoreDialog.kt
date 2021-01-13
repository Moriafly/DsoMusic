package com.dirror.music.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.DialogPlayMoreBinding
import com.dirror.music.ui.activity.FeedbackActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class PlayerMenuMoreDialog(context: Context) : BottomSheetDialog(context, R.style.style_default_dialog) {

    private var binding: DialogPlayMoreBinding = DialogPlayMoreBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
    }

    private var speed = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speed = MyApplication.musicBinderInterface?.getSpeed() ?: 1f

        refreshPitch()

        MyApplication.musicBinderInterface?.getNowSongData()?.let { it ->
            binding.tvSongName.text = it.name
        }

        // 歌曲信息
        binding.itemSongInfo.setOnClickListener {
            SongInfoDialog(context).apply {
                MyApplication.musicBinderInterface?.getNowSongData()?.let { it1 -> setSongData(it1) }
                show()
            }
            // 自己消失
            dismiss()
        }

        // 降噪
        binding.switchNoiseSuppressor.setOnCheckedChangeListener { buttonView, isChecked ->
            // 开启降噪
            val audioSession = MyApplication.musicBinderInterface?.getAudioSessionId()?:0
            // toast("${audioSession}")
            // loge("你好audio: ${audioSession}")
            // AudioEffect.noiseSuppressor(audioSession, isChecked)
            // AudioEffect.automaticGainControl(audioSession, true)
        }

        binding.itemNoiseSuppressor.setOnClickListener {
            binding.switchNoiseSuppressor.isChecked = !binding.switchNoiseSuppressor.isChecked
        }

        binding.ivIncreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.increasePitchLevel()
            refreshPitch()
        }

        binding.ivDecreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.decreasePitchLevel()
            refreshPitch()
        }

        binding.itemSpeed.setOnClickListener {
            MyApplication.musicBinderInterface?.setSpeed(1f)
        }

        // 反馈
        binding.itemFeedback.setOnClickListener {
            val intent = Intent(MyApplication.context, FeedbackActivity::class.java)
            // 从 Content 跳转 Activity 要加 FLAG
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
            // 隐藏 Dialog
            dismiss()
        }


    }

    /**
     * 刷新 Pitch
     */
    private fun refreshPitch() {
        binding.tvPitch.text = MyApplication.musicBinderInterface?.getPitchLevel().toString()
    }


}