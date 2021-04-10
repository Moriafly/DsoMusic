package com.dirror.music.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dirror.music.MyApplication
import com.dirror.music.util.runOnMainThread

class SimpleWorker(context: Context, params : WorkerParameters) : Worker(context , params){
    override fun doWork(): Result {
        val nowPlayState = MyApplication.musicController.value?.isPlaying()?.value?: false
        if (nowPlayState) {
            runOnMainThread{
                MyApplication.musicController.value?.pause()
            }
        }
        return Result.success()
    }
}