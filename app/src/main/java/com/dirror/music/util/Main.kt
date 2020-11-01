package com.dirror.music.util

import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val n = scanner.nextInt()
    val array = arrayOfNulls<Int>(n)
    for (i in 0 until n) {
        array[i] = scanner.nextInt()
    }
    var i = 1
    @rt while (true) {
        for (j in 0..array.lastIndex) {
            if (i == array[j]) {
                break
            }
            if (j == array.lastIndex - 1) {
                println(i)
                return @rt
            }
        }
        i++
    }
}