package com.flash.worker.lib.common.util

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.math.BigDecimal

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CacheCleanUtils
 * Author: Victor
 * Date: 2020/8/21 下午 05:11
 * Description: 清除缓存工具
 * -----------------------------------------------------------------
 */

object CacheCleanUtils {
    @Throws(Exception::class)
    fun getTotalCacheSize(context: Context): String? {
        var cacheSize = getFolderSize(context.cacheDir)
        cacheSize += getSharedPreferenceFolderSize(context)
        cacheSize += getDbFolderSize(context)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return getFormatSize(cacheSize.toDouble())
    }


    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        cleanSharedPreference(context)
        clearAllDb(context)
//        clearDB(context,DbConfig.DATABASE_NAME)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
        }

    }

    fun deleteDir(dir: String?): Boolean {
        return deleteDir(File(dir))
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        //判定空
        return dir?.delete() ?: false
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     *
     * @param context
     */
    fun cleanFiles(context: Context) {
        deleteDir(context.filesDir)
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    fun cleanSharedPreference(context: Context) {
        deleteDir(
            File(
                "/data/data/"
                        + context.packageName + "/shared_prefs"
            )
        )
    }

    fun getSharedPreferenceFolderSize (context: Context):Long {
        return getFolderSize(File("/data/data/" + context.packageName + "/shared_prefs"))
    }


    /**
     * *清除所有数据库(/data/data/包名/databases)
     */
    fun clearAllDb(context: Context) {
        deleteDir(File("/data/data/"+ context.packageName + "/databases"))
    }

    fun clearDb (context: Context,dbName: String) {
        context.deleteDatabase(dbName)
    }

    fun getDbFolderSize (context: Context):Long {
        return getFolderSize(File("/data/data/"+ context.packageName + "/databases"))
    }

    @Throws(Exception::class)
    fun getFolderSize(file: File?): Long {
        var size: Long = 0
        try {
            val fileList = file!!.listFiles()
            for (i in fileList.indices) {
                // 如果下面还有文件
                size = if (fileList[i].isDirectory) {
                    size + getFolderSize(fileList[i])
                } else {
                    size + fileList[i].length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String? {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0KB"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 =
                BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 =
                BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 =
                BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB")
    }

    private fun refreshMedia(context: Context,filePath: String?){
        val packageName = context.packageName
        val authority = "$packageName.provider"
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, authority, File(filePath))
        } else {
            Uri.fromFile(File(filePath))
        }

        var scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri)

        context.sendBroadcast(scanIntent)
    }

}