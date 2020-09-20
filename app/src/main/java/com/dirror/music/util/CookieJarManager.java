package com.dirror.music.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * OkHttp框架Cookie管理类
 * @author 24568
 */

public class CookieJarManager implements CookieJar {

    private final String TAG = "CookieJarManager";

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (null == url || null == cookies || cookies.size() <= 0) {
            return;
        }

        cookieStore.put(url.host(), cookies);

        for (Cookie cookie : cookies) {
            Log.d(TAG, "cookie Name:" + cookie.name());
            Log.d(TAG, "cookie Path:" + cookie.path());
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (null != url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        } else {
            return new ArrayList<Cookie>();
        }
    }
}