package com.dirror.music.music.netease.data

data class BannerData(
    val code: Int,
    val banners: ArrayList<BannersData>
) {
    data class BannersData(
        val pic: String,
        val titleColor: String,
        val typeTitle: String
    )
}
