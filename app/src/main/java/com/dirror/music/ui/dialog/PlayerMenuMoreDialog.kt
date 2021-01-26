package com.dirror.music.ui.dialog

import android.content.Context
import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.databinding.DialogPlayMoreBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayHistoryActivity
import com.dirror.music.ui.base.BaseBottomSheetDialog

class PlayerMenuMoreDialog(context: Context) : BaseBottomSheetDialog(context) {

    private val binding: DialogPlayMoreBinding = DialogPlayMoreBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }


    private var song: StandardSongData? = null

    override fun initView() {

        MyApplication.musicBinderInterface?.getNowSongData()?.let { it ->
            binding.tvSongName.text = it.name
            song = it
        }
    }

    override fun initListener() {
        binding.apply {
            // 添加到本地我喜欢
            itemAddLocalMyFavorite.setOnClickListener {
                song?.let { data ->
                    MyFavorite.addSong(data)
                    dismiss()
                }
            }
            // 歌曲信息
            itemSongInfo.setOnClickListener {
                MyApplication.musicBinderInterface?.getNowSongData()?.let { it1 ->
                    SongInfoDialog(context, it1).show()
                }
                // 自己消失
                dismiss()
            }

            // 播放历史
            itemPlayHistory.setOnClickListener {
                it.context.startActivity(Intent(it.context, PlayHistoryActivity::class.java))
                dismiss()
            }
            // 反馈
//            itemFeedback.setOnClickListener {
//                val intent = Intent(MyApplication.context, FeedbackActivity::class.java)
//                // 从 Content 跳转 Activity 要加 FLAG
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                MyApplication.context.startActivity(intent)
//                // 隐藏 Dialog
//                dismiss()
//            }
        }
    }



}