package com.dirror.music.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.dirror.music.App
import com.dirror.music.databinding.DialogTextInfoBinding
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.room.AppDatabase
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.Secure
import com.dirror.music.util.defaultTypeface
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.sky.SkySecure

class AppInfoDialog(context: Context) : BaseBottomSheetDialog(context) {

    val binding = DialogTextInfoBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()

        binding.apply {
            tvText.typeface = defaultTypeface(App.context)
            tvText.text = """
                [app.build   ] ${getVisionCode()}
                [is debug    ] ${Secure.isDebug()}
                [foyou.ver   ] ${FoyouLibrary.VERSION}
                [database.ver] ${AppDatabase.DATABASE_VERSION}
                [model       ] ${Build.MODEL}
                [android.ver ] ${Build.VERSION.RELEASE}
                [android.sdk ] ${Build.VERSION.SDK_INT}
                [dex.crc     ] ${SkySecure.getDexCrc(App.context)}
                [name.md5    ] ${SkySecure.getMD5("com.dirror.music")}
            """.trimIndent()
        }
    }

}