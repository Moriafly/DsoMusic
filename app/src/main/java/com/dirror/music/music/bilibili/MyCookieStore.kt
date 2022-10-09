package com.dirror.music.music.bilibili

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object MyCookieStore : CookieJar {
    var cookieMap: MutableMap<String, Cookie> = HashMap()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        for (cookie in cookies) {
            cookieMap[cookie.name()] = cookie
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host()
        val cookieList: MutableList<Cookie> = ArrayList()
        for (cookie in cookieMap.values) {
            if (hostMatch(host, cookie.domain())) {
                cookieList.add(cookie)
            }
        }
        return cookieList
    }

    private fun hostMatch(urlHost: String, cookieHost: String): Boolean {
        return urlHost.endsWith(cookieHost)
    }
}