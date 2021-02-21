package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.ui.fragment.HomeFragment
import com.dirror.music.ui.fragment.MyFragment

class MainViewModel: ViewModel() {

    // 状态栏高度
    val statusBarHeight = MutableLiveData<Int>().also {
        it.value = 0
    }

    val myFragment = MyFragment()
    val homeFragment = HomeFragment()

    val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

}