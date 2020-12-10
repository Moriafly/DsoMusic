package com.dirror.music.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.music.netease.data.UserDetailData

class MainViewModel: ViewModel() {

    private val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    private val userBitmap = MutableLiveData<Bitmap>().also {

    }

    private val userDetail = MutableLiveData<UserDetailData>()

    fun getUserId(): LiveData<Long> {
        return userId
    }

    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

}