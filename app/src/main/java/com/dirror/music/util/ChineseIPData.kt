package com.dirror.music.util

import android.content.Context
import android.util.Log
import com.dirror.music.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.lang.Exception
import kotlin.random.Random

class ChineseIPData {

    companion object {

        val TAG = "ChineseIPData"

        private fun genIP(address: String): IP {
            val spit = address.split(".")
            return if (spit.size == 4) {
                IP(spit[0].toInt(), spit[1].toInt(), spit[2].toInt(), spit[3].toInt())
            } else {
                IP(0, 0, 0, 0)
            }
        }

        suspend fun getRandomIP(ctx: Context): String = withContext(Dispatchers.IO) {
            val ipStrSteam = ctx.resources.openRawResource(R.raw.chinese_ip)
            val reader = BufferedReader(ipStrSteam.reader())
            val ipStr = reader.readText()
            val list = ArrayList<Province>()
            val ip : IP
            try {
                reader.close()
                val jsonObject = JSONObject(ipStr)
                val it = jsonObject.keys()
                while (it.hasNext()) {
                    val name = it.next()
                    val ranges = ArrayList<Range>()
                    val province = Province(name = name, list = ranges)
                    list.add(province)
                    val array = jsonObject.getJSONArray(name)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val min = genIP(obj.getString("min"))
                        val max = genIP(obj.getString("max"))
                        val range = Range(min, max)
                        ranges.add(range)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (list.size > 0) {
                val province = list.random()
                val range = province.list.random()
                val max = range.max
                val min = range.min
                val one = if (max.one > min.one) {
                    Random.nextInt(min.one, max.one)
                } else {
                    min.one
                }
                val two = if (max.two > min.two) {
                    Random.nextInt(min.two, max.two)
                } else {
                    min.two
                }
                val three = if (max.three > min.three) {
                    Random.nextInt(min.three, max.three)
                } else {
                    min.three
                }
                val four = if (max.four > min.four) {
                    Random.nextInt(min.four, max.four)
                } else {
                    min.four
                }
                ip = IP(one, two, three, four)
                Log.i(TAG, "get random ip ${province.name}, $ip")
            } else {
                ip = IP(211,86,216,196)
            }
            return@withContext ip.toString()
        }
    }

    data class IP(val one: Int, val two: Int, val three: Int, val four: Int) {
        override fun toString(): String {
            return "$one.$two.$three.$four"
        }
    }

    data class Range(val min: IP, val max: IP)

    data class Province(val name: String, val list: List<Range>)

}