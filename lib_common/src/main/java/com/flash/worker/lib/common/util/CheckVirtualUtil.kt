package com.flash.worker.lib.common.util

import android.util.Log
import java.io.File
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckVirtualUtil
 * Author: Victor
 * Date: 2021/9/7 17:41
 * Description: 检测应用是否被多开
 * -----------------------------------------------------------------
 */
object CheckVirtualUtil {
    val TAG = "CheckVirtualUtil"

    /**
     * 自己的进程有3个，超过三个认为是多开
     * com.sgz.flash.workers
     * com.sgz.flash.workers.core
     * com.sgz.flash.workers.pushcore
     */
    fun isRunInVirtual(): Boolean {
        Loger.e(TAG,"isRunInVirtual()......")
        val filter = getUidStrFormat()
        if (filter == null || filter.length == 0) {
            return false
        }
        val result = CmdUtil.exec("ps")
        if (result == null || result.isEmpty()) {
            return false
        }
        val lines = result.split("\n".toRegex()).toTypedArray()
        if (lines == null || lines.size <= 0) {
            return false
        }
        var exitDirCount = 0
        for (i in lines.indices) {
            if (lines[i].contains(filter)) {
                val pkgStartIndex = lines[i].lastIndexOf(" ")
                val processName = lines[i].substring(
                    if (pkgStartIndex <= 0) 0 else pkgStartIndex + 1,
                    lines[i].length
                )
                val dataFile = File(String.format("/data/data/%s", processName, Locale.CHINA))
                if (dataFile.exists()) {
                    exitDirCount++
                }
            }
        }
        Loger.e(TAG,"isRunInVirtual()......exitDirCount = $exitDirCount")
        return exitDirCount > 3
    }

    fun getUidStrFormat(): String? {
        var filter = CmdUtil.exec("cat /proc/self/cgroup")
        if (filter == null || filter.length == 0) {
            return null
        }
        val uidStartIndex = filter.lastIndexOf("uid")
        var uidEndIndex = filter.lastIndexOf("/pid")
        if (uidStartIndex < 0) {
            return null
        }
        if (uidEndIndex <= 0) {
            uidEndIndex = filter.length
        }
        filter = filter.substring(uidStartIndex + 4, uidEndIndex)
        try {
            val strUid = filter.replace("\n".toRegex(), "")
            if (isNumericZidai(strUid)) {
                val uid = Integer.valueOf(strUid)
                filter = String.format("u0_a%d", uid - 10000)
                return filter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun isNumericZidai(str: String?): Boolean {
        if (str == null || str.length == 0) {
            return false
        }
        for (i in 0 until str.length) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }
}