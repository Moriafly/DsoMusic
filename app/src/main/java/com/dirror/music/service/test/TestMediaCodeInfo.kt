package com.dirror.music.service.test

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import org.jetbrains.annotations.TestOnly

/**
 * 提供有关设备上可用的给定媒体编解码器的信息。
 * 您可以通过查询来迭代所有可用的编解码器。
 * 例如，下面提供如何查找支持给定 MIME 类型的编码器：MediaCodecList
 * [selectCodec]
 * @author Moriafly
 */
object TestMediaCodeInfo {

    fun test() {

    }

    /**
     *
     */
    private fun selectCodec(mimeType: String): MediaCodecInfo? {
        val numCodecs = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            // val info = MediaCodecList()
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
            if (!codecInfo.isEncoder) {
                continue
            }
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    return codecInfo
                }
            }
        }
        return null
    }

    /**
     * 获取所有媒体 编 / 解 码器 的信息
     */
    @TestOnly
    fun getCodec(): ArrayList<MediaCodecInfo> {
        val numCodecs = MediaCodecList.getCodecCount()
        val list = ArrayList<MediaCodecInfo>()
        for (i in 0 until numCodecs) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
//            // 过滤 encode
//            if (!codecInfo.isEncoder) {
//                continue
//            }
            list.add(codecInfo)
        }
        return list
    }

}