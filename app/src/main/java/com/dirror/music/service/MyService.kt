package com.dirror.music.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : LifecycleService() {

    private var number = 0

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            while (true) {
                delay(1_000)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        TODO("Return the communication channel to the service.")
    }
}
