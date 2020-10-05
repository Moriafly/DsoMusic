package com.dirror.music.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.music.netease.BannerUtil
import com.dirror.music.music.netease.BannerUtil.BannerData
import com.dirror.music.util.runOnMainThread

class HomeViewModel: ViewModel() {
    private val _bannerDataListLive = MutableLiveData<List<BannerData>>()
    val bannerDataListLive: LiveData<List<BannerData>> // 对外
        get() = _bannerDataListLive

    fun getBannerDataList() {
        BannerUtil.getBanner {

            runOnMainThread {
                _bannerDataListLive.value = it.toList()
            }
        }
    }
}