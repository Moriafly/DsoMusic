package com.dirror.music.util

object Utils {

    fun toMap(vararg params: String): Map<String, String> {
        val map = HashMap<String, String>()
        var i = 0
        while (i < params.size) {
            map[params[i]] = params[i + 1]
            i += 2
        }
        return map
    }

}