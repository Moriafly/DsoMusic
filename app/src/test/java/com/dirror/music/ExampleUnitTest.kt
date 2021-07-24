package com.dirror.music

import com.dirror.music.util.Utils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getUrl_isCorrect() {
        val url = Utils.getNeteasePicUrl(109951162868128395)
        assertEquals("https://p3.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg", url)
    }
}