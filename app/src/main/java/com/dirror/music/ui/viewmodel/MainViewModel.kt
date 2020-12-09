package com.dirror.music.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication

class MainViewModel: ViewModel() {

    private val userId =  MutableLiveData<Long>() .also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    fun getUserId(): LiveData<Long> {
        return userId
    }

    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

}