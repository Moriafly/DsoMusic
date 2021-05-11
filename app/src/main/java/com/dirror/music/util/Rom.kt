package com.dirror.music.util

import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object Rom {

    var meizu: Boolean = false

    const val TAG = "Rom"

    private fun isMeizuRom(): Boolean {
        //return Build.MANUFACTURER.contains("Meizu");
        val meizuFlymeOSFlag: String = getSystemProperty("ro.build.display.id") ?: ""
        return if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            false
        } else meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase()
            .contains("flyme")
    }

    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read sysprop $propName", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception while closing InputStream", e)
                }
            }
        }
        return line
    }

    init {
        meizu = isMeizuRom()
    }

}