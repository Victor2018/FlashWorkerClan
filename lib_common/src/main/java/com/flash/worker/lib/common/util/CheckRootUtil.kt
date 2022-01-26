package com.flash.worker.lib.common.util

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.*
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckRootUtil
 * Author: Victor
 * Date: 2021/9/7 17:45
 * Description: 检测设备是否root
 * -----------------------------------------------------------------
 */
object CheckRootUtil {
    const val TAG: String = "CheckRootUtil"

    fun isDeviceRooted(): Boolean {
        if (checkDeviceDebuggable()) {
            return true
        } //check buildTags
        if (checkSuperuserApk()) {
            return true
        } //Superuser.apk
        if (checkRootPathSU()) {
            return true
        } //find su in some path
        if (checkRootWhichSU()) {
            return true
        }
        if (checkAccessRootData()) {
            return true
        }
        return false
    }

    fun checkDeviceDebuggable(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.i(TAG, "buildTags=$buildTags")
            return true
        }
        return false
    }

    fun checkSuperuserApk(): Boolean {
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                Log.i(TAG, "/system/app/Superuser.apk exist")
                return true
            }
        } catch (e: Exception) {
        }
        return false
    }

    fun checkRootPathSU(): Boolean {
        var f: File? = null
        val kSuSearchPaths = arrayOf(
            "/system/bin/",
            "/system/xbin/",
            "/system/sbin/",
            "/sbin/",
            "/vendor/bin/"
        )
        try {
            for (i in kSuSearchPaths.indices) {
                f = File(kSuSearchPaths[i] + "su")
                if (f != null && f.exists()) {
                    Log.i(TAG, "find su in : " + kSuSearchPaths[i])
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun checkRootWhichSU(): Boolean {
        val strCmd = arrayOf("/system/xbin/which", "su")
        val execResult = executeCommand(strCmd)
        if (execResult != null) {
            Log.i(TAG, "execResult=$execResult")
            return true
        }
        return false
    }

    fun executeCommand(shellCmd: Array<String>?): ArrayList<String?>? {
        var line: String? = null
        val fullResponse = ArrayList<String?>()
        var localProcess: Process? = null
        localProcess = try {
            Log.i(TAG, "to shell exec which for find su :")
            Runtime.getRuntime().exec(shellCmd)
        } catch (e: Exception) {
            return null
        }
        val out = BufferedWriter(OutputStreamWriter(localProcess?.outputStream))
        val reader = BufferedReader(InputStreamReader(localProcess?.inputStream))
        try {
            while (reader.readLine().also { line = it } != null) {
                Log.i(TAG, "–> Line received: $line")
                fullResponse.add(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.i(TAG, "–> Full response was: $fullResponse")
        return fullResponse
    }

    fun checkAccessRootData(): Boolean {
        try {
            Log.i(TAG, "to write /data")
            val fileContent = "test_ok"
            val writeFlag = writeFile("/data/su_test", fileContent)
            if (writeFlag) {
                Log.i(TAG, "write ok")
            } else {
                Log.i(TAG, "write failed")
            }
            Log.i(TAG, "to read /data")
            val strRead = readFile("/data/su_test")
            Log.i(TAG, "strRead=$strRead")
            return TextUtils.equals(fileContent,strRead)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    //写文件
    fun writeFile(fileName: String?, message: String): Boolean {
        try {
            val fout = FileOutputStream(fileName)
            val bytes = message.toByteArray()
            fout.write(bytes)
            fout.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    //读文件
    fun readFile(fileName: String?): String? {
        val file = File(fileName)
        try {
            val fis = FileInputStream(file)
            val bytes = ByteArray(1024)
            val bos = ByteArrayOutputStream()
            var len: Int
            while (fis.read(bytes).also { len = it } > 0) {
                bos.write(bytes, 0, len)
            }
            val result = String(bos.toByteArray())
            Log.i(TAG, result)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}