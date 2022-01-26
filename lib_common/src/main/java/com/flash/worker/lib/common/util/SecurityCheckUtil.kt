package com.flash.worker.lib.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import android.util.Log
import com.flash.worker.lib.coremodel.util.AppConfig
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File:  SecurityCheckUtil
 * Author: Victor
 * Date: 2021/9/6 14:47
 * Description: 
 * -----------------------------------------------------------------
 */

object SecurityCheckUtil {
    const val TAG = "SecurityCheckUtil"

    fun isDeviceSecurity (context: Context): Boolean {
        val isRoot = CheckRootUtil.isDeviceRooted()
        val isHook = CheckHookUtil.isHook(context)
        var isEmulator = EmulatorUtil.isEmulator(context)
//        val isRunInVirtual = CheckVirtualUtil.isRunInVirtual()
        Loger.e(TAG,"isDeviceSecurity-isRoot = $isRoot")
        Loger.e(TAG,"isDeviceSecurity-isHook = $isHook")
        Loger.e(TAG,"isDeviceSecurity-isEmulator = $isEmulator")
//        Loger.e(TAG,"isDeviceSecurity-isRunInVirtual = $isRunInVirtual")
        return !isRoot && !isHook && !isEmulator
    }

}