package com.flash.worker.lib.common.data

import java.io.File
import java.math.BigDecimal


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ProgressInfo
 * Author: Victor
 * Date: 2021/8/31 11:37
 * Description: 
 * -----------------------------------------------------------------
 */
class ProgressInfo {
    var downloadUrl: String? = null//下载地址
    var savePath: String? = null//文件保存路径
    var filePath: String? = null//文件位置
    var downloadSize: Long = 0//下载进度
    var totalSize: Long = 0//文件大小

    /**
     * 下载进度百分比
     */
    fun percent(): Double {
        return downloadSize ratio totalSize
    }

    fun getUrlFileName(): String {
        downloadUrl ?: return "unknownfile_${System.currentTimeMillis()}"
        var filename: String? = null
        val strings = downloadUrl!!.split("/").toTypedArray()
        for (string in strings) {
            if (string.contains("?")) {
                val endIndex = string.indexOf("?")
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex)
                    return filename
                }
            }
        }
        if (strings.isNotEmpty()) {
            filename = strings[strings.size - 1]
        }
        filename ?: return "unknownfile_${System.currentTimeMillis()}"
        return filename
    }

    fun getFile(): File {
        return File(filePath)
    }

    infix fun Long.ratio(bottom: Long): Double {
        if (bottom <= 0) {
            return 0.0
        }
        val result = (this * 100.0).toBigDecimal()
            .divide((bottom * 1.0).toBigDecimal(), 2, BigDecimal.ROUND_FLOOR)
        return result.toDouble()
    }
}