package com.dirror.music.service

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dirror.music.App
import com.dirror.music.util.runOnMainThread
import java.util.concurrent.TimeUnit

class SimpleWorker(context: Context, params : WorkerParameters) : Worker(context , params){
    override fun doWork(): Result {
        App.musicController.value?.apply {
            val nowPlayState = isPlaying().value ?: false
            if (nowPlayState) {
                if (getTimingOffMode()){
                    val time = (getDuration() - getProgress())/1000
                    Log.d("lbcc",time.toString())
                    if (time > 0){
                        val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay((time).toLong(), TimeUnit.SECONDS)
                            .addTag("lbccc").build()
                        WorkManager.getInstance(applicationContext).enqueue(request)
                    }else{
                        pauseAndRefresh()
                        return Result.success()
                    }
                }else{
                    Log.d("lbcc","pause right now")
                    pauseAndRefresh()
                }
            }
        }
        return Result.success()
    }

    private fun pauseAndRefresh(){
        App.musicController.value?.apply{
            runOnMainThread{
                pause()
            }
            setCurrentCustom(0)
            setCurrentRight(0)
        }
    }
}