package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import com.dirror.music.api.StandardGET
import com.dirror.music.music.dirror.SearchSong
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.parseSize
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.dialog_song_info.*

class SongInfoDialog: Dialog {

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_song_info)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clDialog.setOnClickListener {
            // dismiss()
        }
    }

    /**
     * 传入 songData
     */
    fun setSongData(songData: StandardSongData) {
        songData.let {
            when (it.source) {
                SOURCE_NETEASE -> {
                    StandardGET.getSongInfo(it.id.toString()) {data ->
                        runOnMainThread {
                            valueViewId.setValue(it.id.toString())
                            if (SearchSong.getDirrorSongUrl(it.id.toString()) != "") {
                                valueViewSource.setValue("Dirror 音乐")
                            } else {
                                valueViewSource.setValue("网易云音乐")
                            }
                            valueViewBitrate.setValue("${data.br/1000} kbps")
                            valueViewSize.setValue(data.size.parseSize())
                            valueViewType.setValue(data.type?:"未知")
                        }
                    }
                }
                SOURCE_QQ -> {
                    runOnMainThread {
                        valueViewId.setValue(it.id.toString())
                        if (SearchSong.getDirrorSongUrl(it.id.toString()) != "") {
                            valueViewSource.setValue("Dirror 音乐")
                        } else {
                            valueViewSource.setValue("QQ 音乐")
                        }
                        valueViewBitrate.setValue("未知")
                        valueViewSize.setValue("未知")
                        valueViewType.setValue("未知")
                    }
                }
                SOURCE_LOCAL -> {
                    runOnMainThread {
                        valueViewId.setValue(it.id.toString())
                        valueViewSource.setValue(context.getString(R.string.local_music))
                        valueViewBitrate.setValue("未知")
                        val size = songData.localInfo?.size ?: 0L
                        valueViewSize.setValue(size.parseSize())
                        valueViewType.setValue("未知")
                    }
                }

            }

        }
    }

}