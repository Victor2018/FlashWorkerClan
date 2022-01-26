package com.flash.worker.lib.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import java.io.BufferedReader
import java.io.FileReader
import java.util.HashSet


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckHookUtil
 * Author: Victor
 * Date: 2021/9/7 17:51
 * Description: hook检测
 * -----------------------------------------------------------------
 */
object CheckHookUtil {
    const val TAG = "CheckHookUtil"
    fun isHook(context: Context): Boolean {
        return isHookByPackageName(context) || isHookByStack() || isHookByJar()
    }

    /**
     * 包名检测
     *
     * @param context
     * @return
     */
    fun isHookByPackageName(context: Context): Boolean {
        var isHook = false
        val packageManager = context.packageManager
        val applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                ?: return isHook
        for (applicationInfo in applicationInfoList) {
            if (applicationInfo.packageName == "de.robv.android.xposed.installer") {
                Loger.e(TAG, "Xposed found on the system.")
                isHook = true
            }
            if (applicationInfo.packageName == "com.saurik.substrate") {
                isHook = true
                Loger.e(TAG, "Substrate found on the system.")
            }
        }
        return isHook
    }

    fun isHookByStack(): Boolean {
        var isHook = false
        try {
            throw Exception("blah")
        } catch (e: Exception) {
            var zygoteInitCallCount = 0
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                    zygoteInitCallCount++
                    if (zygoteInitCallCount == 2) {
                        Loger.e(TAG, "Substrate is active on the device.")
                        isHook = true
                    }
                }
                if (stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked") {
                    Loger.e(TAG,
                        "A method on the stack trace has been hooked using Substrate.")
                    isHook = true
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main") {
                    Loger.e(TAG, "Xposed is active on the device.")
                    isHook = true
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod") {
                    Loger.e(TAG, "A method on the stack trace has been hooked using Xposed.")
                    isHook = true
                }
            }
        }
        return isHook
    }

    fun isHookByJar(): Boolean {
        var isHook = false
        try {
            val libraries: MutableSet<String> = HashSet()
            val mapsFilename = "/proc/" + Process.myPid() + "/maps"
            val reader =
                BufferedReader(FileReader(mapsFilename))
            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    val n = line.lastIndexOf(" ")
                    libraries.add(line.substring(n + 1))
                }
            }
            for (library in libraries) {
                if (library!!.contains("com.saurik.substrate")) {
                    Loger.e(TAG, "Substrate shared object found: $library")
                    isHook = true
                }
                if (library.contains("XposedBridge.jar")) {
                    Loger.e(TAG, "Xposed JAR found: $library")
                    isHook = true
                }
            }
            reader.close()
        } catch (e: Exception) {
            Loger.e(TAG, e.toString())
        }
        return isHook
    }
}