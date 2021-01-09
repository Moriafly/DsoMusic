package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.ui.fragment.HomeFragment
import com.dirror.music.ui.fragment.MyFragment

class MainViewModel: ViewModel() {

    val myFragment = MyFragment()
    val homeFragment = HomeFragment()

    val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    private val userDetail = MutableLiveData<UserDetailData>()

    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

}