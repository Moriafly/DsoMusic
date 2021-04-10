package com.dirror.music.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.DialogTimingOffBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class TimingOffDialog (context: Context) : BottomSheetDialog(context, R.style.style_default_dialog){
    private var binding: DialogTimingOffBinding = DialogTimingOffBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            when(MyApplication.musicController.value?.getCurrentRight()){
                0 -> time0.setRight()
                1 -> time10.setRight()
                2 -> time20.setRight()
                3 -> time30.setRight()
                4 -> time45.setRight()
                5 -> time60.setRight()
                6 -> {
                    if (MyApplication.musicController.value?.getCurrentCustom() != 0){
                        timeCustom.setTitle("自定义（${MyApplication.musicController.value?.getCurrentCustom()}分钟后）")
                        timeCustom.setRight()
                    }
                }
            }
            time0.setOnClickListener { chooseThisOne(0) }
            time10.setOnClickListener { chooseThisOne(1) }
            time20.setOnClickListener { chooseThisOne(2) }
            time30.setOnClickListener { chooseThisOne(3) }
            time45.setOnClickListener { chooseThisOne(4) }
            time60.setOnClickListener { chooseThisOne(6) }
            timeCustom.setOnClickListener { chooseThisOne(6) }

        }

    }
    private fun chooseThisOne(int: Int){
        MyApplication.musicController.value?.setCurrentRight(int)
        if (int == 6) MyApplication.musicController.value?.setCurrentCustom(3)
        dismiss()
    }
}