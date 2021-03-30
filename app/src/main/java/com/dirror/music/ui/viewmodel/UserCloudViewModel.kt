package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.music.netease.UserCloud
import com.dirror.music.music.netease.data.toStandard
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.runOnMainThread
import com.dso.ext.toSizeFormat

class UserCloudViewModel : ViewModel() {

    var songlist = MutableLiveData<ArrayList<StandardSongData>>().also {
        it.value = ArrayList()
    }

    var size = MutableLiveData<String>().also {
        it.value = ""
    }

    private var offset = 0
    private var isLoading = false // 是否正在加载
    private var isFinish = false

    /**
     * 获取数据
     */
    fun fetchData() {
        if (isLoading || isFinish) {
            return
        }
        isLoading = true
        // request data
        UserCloud.getUserCloud(offset, {
            if (it.hasMore) {
                offset += 50
            } else {
                isFinish = true
            }
            runOnMainThread {
                if (size.value.isNullOrEmpty()) {
                    size.value = "${(it.size.toLongOrNull() ?: 0L).toSizeFormat()} / ${(it.maxSize.toLongOrNull() ?: 0L).toSizeFormat()}"
                }
                songlist.value = arrayListOf(
                    songlist.value!!.toList(),
                    it.data.toStandard().toList()
                ).flatten() as ArrayList<StandardSongData>
            }
            isLoading = false
        }, {
            ErrorCode.toast(it)
        })
    }

}