package com.dirror.music.api

object CloudMusicApi {


    /**
     * 默认搜索关键词
     * 说明 : 调用此接口 , 可获取默认搜索关键词
     */
    const val SEARCH_DEFAULT = "${API_AUTU}/search/default"

    /**
     * 首页-发现说明 : 调用此接口 , 可获取APP首页信息
     * 可选参数 : refresh: 是否刷新数据,默认为true
     */
    const val HOME_FIND = "${API_AUTU}/homepage/block/page"

    /**
     * 热搜列表(详细)
     * 说明 : 调用此接口,可获取热门搜索列表
     */
    const val SEARCH_HOT_DETAIL = "${API_AUTU}/search/hot/detail"

}