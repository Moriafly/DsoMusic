package com.dirror.music.util

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import com.dirror.music.App
import java.io.File
import java.math.BigDecimal
import kotlin.concurrent.thread

/**
 * 缓存管理
 */
object ImageCacheManager {

    /**
     * 获取图片缓存大小
     */
    fun getImageCacheSize(): String {
        val size = getCacheSize("/image_cache") + getCacheSize("/image_manager_disk_cache")
        return getFormatSize(size.toDouble())
    }

    /**
     * 清除所有图片缓存
     */
    fun clearImageCache(success: () -> Unit) {
        thread {
            try {
                deleteFolderFile(App.context.cacheDir.path + "/image_cache", true)
                deleteFolderFile(App.context.cacheDir.path + "/image_manager_disk_cache", true)
            } catch (e: Exception) {

            }
            runOnMainThread {
                success()
            }
        }
    }

    /**
     * 清除图片磁盘缓存
     */
    private fun clearImageDiskCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread {
                    // Glide.get(context!!).clearDiskCache()
                    // BusUtil.getBus().post(new GlideCacheClearSuccessEvent());
                }.start()
            } else {
                // Glide.get(context!!).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片内存缓存
     */
    private fun clearImageMemoryCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                //  Glide.get(context!!).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片所有缓存
     */
//    fun clearImageAllCache(context: Context) {
//        clearImageDiskCache(context)
//        clearImageMemoryCache(context)
//        val imageExternalCatchDir =
//            context.externalCacheDir.toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
//        deleteFolderFile(imageExternalCatchDir, true)
//    }

    /**
     * 获取缓存大小
     * @return CacheSize
     */
    private fun getCacheSize(cacheDirName: String): Long {
        try {
            val file = File(App.context.cacheDir.path + cacheDirName)
            return getFolderSize(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()!!
            for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath filePath
     * @param deleteThisPath deleteThisPath
     */
    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()!!
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().isEmpty()) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 格式化单位
     * @param size size
     * @return size
     */
    private fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "$size Byte"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB"
    }


}