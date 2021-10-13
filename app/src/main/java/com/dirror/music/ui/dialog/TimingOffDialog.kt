package com.dirror.music.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.databinding.DialogTimingOffBinding
import com.dirror.music.service.SimpleWorker
import com.dirror.music.ui.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.concurrent.TimeUnit

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
            when(App.musicController.value?.getCurrentRight()){
                0 -> time0.setRight()
                10 -> time10.setRight()
                20 -> time20.setRight()
                30 -> time30.setRight()
                45 -> time45.setRight()
                60 -> time60.setRight()
                10086 -> {
                    val time = App.musicController.value?.getCurrentCustom()
                    if (time != 0 && time != null){
                        if (time /60 != 0 ){
                            timeCustom.setTitle("自定义（${time/60}小时${time%60}分钟后）")
                        }else{
                            timeCustom.setTitle("自定义（${time}分钟后）")
                        }
                        timeCustom.setRight()
                    }
                }
            }
            time0.setOnClickListener {
                App.musicController.value?.setCurrentRight(0)
                App.musicController.value?.setCurrentCustom(0)
                Toast.makeText(context, "定时停止播放已取消", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            time10.setOnClickListener { chooseThisOne(10) }
            time20.setOnClickListener { chooseThisOne(20) }
            time30.setOnClickListener { chooseThisOne(30) }
            time45.setOnClickListener { chooseThisOne(45) }
            time60.setOnClickListener { chooseThisOne(60) }
            timeCustom.setOnClickListener {
                App.musicController.value?.setCurrentRight(10086)
                dismiss()
                CustomTimeFragment().show(PlayerViewModel.fragmentManager,"android")
            }
            switcherTimingOff.setChecked(App.musicController.value?.getTimingOffMode() == true)
            switcherTimingOff.setOnCheckedChangeListener {
                if (it){
                    timingOffMode.setTextColor(Color.BLACK)
                }else{
                    timingOffMode.setTextColor(Color.GRAY)
                }
                App.musicController.value?.setTimingOffMode(it)
            }
        }

    }
    private fun chooseThisOne(int: Int){
        App.musicController.value?.setCurrentRight(int)
        App.musicController.value?.setCurrentCustom(int)
        Toast.makeText(context, "设置成功，将于$int 分钟后关闭 ", Toast.LENGTH_SHORT).show()

        val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay((int).toLong(), TimeUnit.MINUTES)
            .addTag("lbccc").build()
        WorkManager.getInstance(context).enqueue(request)

        dismiss()
    }
}