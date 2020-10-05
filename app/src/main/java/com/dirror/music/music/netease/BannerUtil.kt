package com.dirror.music.music.netease

import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson

object BannerUtil {
    fun getBanner(success: (ArrayList<BannerData>) -> Unit) {
        loge("getBanner")
        val url = "${API_MUSIC_ELEUU}/banner?type=1"
        MagicHttp.OkHttpManager().newGet(url, {
            val bannersData = Gson().fromJson(it, BannersData::class.java)
            loge("Banner Code:${bannersData.code}")
            if (bannersData.code == 200) {
                success.invoke(bannersData.banners)
            }
        }, {

        })
    }

    data class BannersData(
        val banners: ArrayList<BannerData>,
        val code: Int
    )

    data class BannerData(
        val pic: String
    )
}