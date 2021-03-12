package com.dirror.music.util

object ErrorCode {

    const val ERROR_MAGIC_HTTP = 0 // MagicHttp 错误
    const val ERROR_JSON = 1 // Json 解析错误
    const val ERROR_NOT_COOKIE = 2 // 不存在 Cookie

    fun toast(code: Int) {
        toast(getMessage(code))
    }

    fun getMessage(code: Int): String {
        val message = when (code) {
            ERROR_MAGIC_HTTP -> "MagicHttp 错误，请检查网络或者稍后请求"
            ERROR_JSON -> "Json 解析错误，请向开发者反馈"
            ERROR_NOT_COOKIE -> "UID 离线状态无法使用此功能，请使用手机号等方式登录"
            else -> "未知错误"
        }
        return "错误代码：${code}，${message}"
    }

}