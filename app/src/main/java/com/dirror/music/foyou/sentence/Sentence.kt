package com.dirror.music.foyou.sentence

import com.dirror.foyou.sentence.SentenceData
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.MyApplication
import com.dirror.music.util.InternetState
import com.dirror.music.util.MagicHttp

object Sentence {
    /**
     * 获取句子
     */
    fun getSentence(success: (SentenceData) -> Unit) {
        if (InternetState.isInternetAvailable(MyApplication.context)) { // 有网络
            when ((1..5).random()) {
                in 1..3 -> getHitokotoLibrarySentence() {
                    success.invoke(it)
                }
                else -> {
                    success.invoke(getFoyouLibrarySentence())
                }
            }
        } else { // 无网络
            success.invoke(getFoyouLibrarySentence())
        }
    }

    /**
     * 获取 Foyou 库句子
     */
    private fun getFoyouLibrarySentence(): SentenceData {
        return FoyouLibrary.getSentence()
    }

    /**
     * 获取一言库句子
     */
    private fun getHitokotoLibrarySentence(success: (SentenceData) -> Unit) {
        val url = "https://v1.hitokoto.cn/?encode=json"
        MagicHttp.OkHttpManager().newGet(url, {
            val str = it
            val mainStr = str.substring(str.indexOf("hitokoto",0) + 11,str.indexOf("type",0) -3 )
            val fromWhoStr = str.substring(str.indexOf("from_who",0) + 11,str.indexOf("creator",0) -3 )
            val fromStr = str.substring(str.indexOf("from",0) + 7,str.indexOf("from_who",0) -3 )
            success.invoke(OptimizeHitokoto.optimizeHitokoto(SentenceData(mainStr, fromWhoStr, fromStr)))
        }, {

        })

    }

}