package com.dirror.music.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.DialogPlayMoreBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.FeedbackActivity
import com.dirror.music.ui.activity.PlayHistoryActivity
import com.dirror.music.util.toast
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
    private var song: StandardSongData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speed = MyApplication.musicBinderInterface?.getSpeed() ?: 1f

        refreshPitch()

        MyApplication.musicBinderInterface?.getNowSongData()?.let { it ->
            binding.tvSongName.text = it.name
            song = it
        }

        // 添加到本地我喜欢
        binding.itemAddLocalMyFavorite.setOnClickListener {
            song?.let { data ->
                MyFavorite.addSong(data)
                toast("添加成功")
            }
        }

        // 歌曲信息
        binding.itemSongInfo.setOnClickListener {
            MyApplication.musicBinderInterface?.getNowSongData()?.let { it1 ->
                SongInfoDialog(context, it1).show()
            }
            // 自己消失
            dismiss()
        }

        binding.ivIncreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.increasePitchLevel()
            refreshPitch()
        }

        binding.ivDecreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.decreasePitchLevel()
            refreshPitch()
        }
        // 播放历史
        binding.itemPlayHistory.setOnClickListener {
            it.context.startActivity(Intent(it.context, PlayHistoryActivity::class.java))
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