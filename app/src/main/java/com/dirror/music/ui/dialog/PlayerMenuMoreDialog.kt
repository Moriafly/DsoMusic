package com.dirror.music.ui.dialog

import android.content.Context
import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.databinding.DialogPlayMoreBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayHistoryActivity
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.toast

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
            // 添加到网易云我喜欢
            itemAddNeteaseFavorite.setOnClickListener {
                if (MyApplication.userManager.getCloudMusicCookie().isEmpty()) {
                    toast("离线模式无法收藏到在线我喜欢~")
                } else {
                    song?.let {
                        when (it.source) {
                            SOURCE_NETEASE -> {
                                MyApplication.cloudMusicManager.likeSong(it.id, {
                                    toast("添加到我喜欢成功")
                                }, {
                                    toast("添加到我喜欢失败")
                                })
                            }
                            SOURCE_QQ -> {
                                toast("暂不支持此音源")
                            }
                        }
                    }
                }
            }
            // 歌曲信息
            itemSongInfo.setOnClickListener {
                MyApplication.musicBinderInterface?.getNowSongData()?.let { it1 ->
                    SongInfoDialog(context, it1).show()
                }
                dismiss()
            }

            // 播放历史
            itemPlayHistory.setOnClickListener {
                it.context.startActivity(Intent(it.context, PlayHistoryActivity::class.java))
                dismiss()
            }
        }
    }



}