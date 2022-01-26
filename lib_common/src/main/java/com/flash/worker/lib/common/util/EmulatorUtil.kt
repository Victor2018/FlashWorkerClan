package com.flash.worker.lib.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.opengl.GLES20
import android.os.Environment
import android.text.TextUtils
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File:  EmulatorUtil
 * Author: Victor
 * Date: 2021/9/6 11:44
 * Description: 模拟器检测工具
 * -----------------------------------------------------------------
 */

object EmulatorUtil {
    const val TAG = "EmulatorUtil"
    const val RESULT_MAYBE_EMULATOR = 0 //可能是模拟器
    const val RESULT_EMULATOR = 1 //模拟器
    const val RESULT_UNKNOWN = 2 //可能是真机

    fun isEmulator (context: Context): Boolean {
        var suspectCount = 0

        //检测硬件名称
        val hardwareResult = checkFeaturesByHardware()
        if (hardwareResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (hardwareResult == RESULT_EMULATOR) {
            return true
        }

        //检测渠道
        val flavorResult = checkFeaturesByFlavor()
        if (flavorResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (flavorResult == RESULT_EMULATOR) {
            return true
        }

        //检测设备型号
        val modelResult = checkFeaturesByModel()
        if (modelResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (modelResult == RESULT_EMULATOR) {
            return true
        }

        //检测硬件制造商
        val manufacturerResult = checkFeaturesByManufacturer()
        if (manufacturerResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (manufacturerResult == RESULT_EMULATOR) {
            return true
        }

        //检测主板名称
        val boardResult = checkFeaturesByBoard()
        if (boardResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (boardResult == RESULT_EMULATOR) {
            return true
        }
        //检测主板平台
        val platformResult = checkFeaturesByPlatform()
        if (platformResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (platformResult == RESULT_EMULATOR) {
            return true
        }

        //检测基带信息
        val baseBandResult = checkFeaturesByBaseBand()
        if (baseBandResult == RESULT_MAYBE_EMULATOR) {
            suspectCount++
        } else if (baseBandResult == RESULT_EMULATOR) {
            return true
        }

        //检测蓝叠模拟器
        val blueStacksResult = checkBlueStacks()
        if (blueStacksResult == RESULT_EMULATOR) {
            return true
        }

        //检测传感器数量
        val sensorNumber: Int = getSensorNumber(context)
        if (sensorNumber <=7) {
            suspectCount++
        }

        //检测已安装第三方应用数量
        val userAppNumber: Int = getUserAppNumber()
        if (userAppNumber <= 5) {
            suspectCount++
        }

        //检测是否支持闪光灯
        val supportCameraFlash: Boolean = supportCameraFlash(context)
        if (!supportCameraFlash) {
            suspectCount++
        }

        //检测是否支持相机
        val supportCamera: Boolean = supportCamera(context)
        if (!supportCamera) {
            suspectCount++
        }

        //检测是否支持蓝牙
        val supportBluetooth: Boolean = supportBluetooth(context)
        if (!supportBluetooth) suspectCount++

        //检测光线传感器
        val hasLightSensor: Boolean = hasLightSensor(context)
        if (!hasLightSensor) {
            suspectCount++
        }

        //检测进程组信息
        val cgroupResult = checkFeaturesByCgroup()
        if (cgroupResult === RESULT_MAYBE_EMULATOR) {
            suspectCount++
        }
        Loger.e(TAG,"isEmulator-suspectCount = $suspectCount")
        //嫌疑值大于3，认为是模拟器
        return suspectCount > 3
    }

    /**
     * 特征参数-硬件名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByHardware(): Int {
        val hardware: String = CmdUtil.getProperty("ro.hardware") ?: return RESULT_MAYBE_EMULATOR
        val result: Int
        val tempValue = hardware.toLowerCase()

        Loger.e(TAG,"checkFeaturesByHardware-tempValue = $tempValue")
        result = when (tempValue) {
            "ttvm", "nox", "cancro", "intel", "vbox", "vbox86", "android_x86" -> RESULT_EMULATOR
            else -> RESULT_UNKNOWN
        }
        return result
    }

    /**
     * 特征参数-渠道
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByFlavor(): Int {
        val flavor: String = CmdUtil.getProperty("ro.build.flavor")
                ?: return RESULT_MAYBE_EMULATOR
        val result: Int
        val tempValue = flavor.toLowerCase()
        Loger.e(TAG,"checkFeaturesByFlavor-tempValue = $tempValue")

        if (tempValue.contains("vbox")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("sdk_gphone")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("xx-user")) {
            result = RESULT_EMULATOR
        }
        else {
            result = RESULT_UNKNOWN
        }
        return result
    }

    /**
     * 特征参数-设备型号
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByModel(): Int {
        val model: String = CmdUtil.getProperty("ro.product.model")
                ?: return RESULT_MAYBE_EMULATOR
        val result: Int
        val tempValue = model.toLowerCase()
        Loger.e(TAG,"checkFeaturesByModel-tempValue = $tempValue")
        if (tempValue.contains("google_sdk")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("emulator")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("android sdk built for x86")) {
            result = RESULT_EMULATOR
        } else result = RESULT_UNKNOWN
        return result
    }

    /**
     * 特征参数-硬件制造商
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByManufacturer(): Int? {
        val manufacturer: String = CmdUtil.getProperty("ro.product.manufacturer")
                ?: return RESULT_MAYBE_EMULATOR
        val result: Int
        val tempValue = manufacturer.toLowerCase()
        Loger.e(TAG,"checkFeaturesByManufacturer-tempValue = $tempValue")
        if (tempValue.contains("genymotion")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("netease")) {
            result = RESULT_EMULATOR
        } //网易MUMU模拟器
        else result = RESULT_UNKNOWN
        return result
    }

    /**
     * 特征参数-主板名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByBoard(): Int {
        val board: String = CmdUtil.getProperty("ro.product.board")
                ?: return RESULT_MAYBE_EMULATOR
        val result: Int
        val tempValue = board.toLowerCase()
        Loger.e(TAG,"checkFeaturesByBoard-tempValue = $tempValue")
        if (tempValue.contains("android")) {
            result = RESULT_EMULATOR
        } else if (tempValue.contains("goldfish")) {
            result = RESULT_EMULATOR
        } else {
            result = RESULT_UNKNOWN
        }
        return result
    }

    /**
     * 特征参数-主板平台
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByPlatform(): Int {
        val platform: String = CmdUtil.getProperty("ro.board.platform")
                ?: return RESULT_MAYBE_EMULATOR
        var result = RESULT_UNKNOWN
        val tempValue = platform.toLowerCase()
        Loger.e(TAG,"checkFeaturesByPlatform-tempValue = $tempValue")
        if (tempValue.contains("android")) {
            result = RESULT_EMULATOR
        }
        return result
    }

    /**
     * 特征参数-基带信息
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private fun checkFeaturesByBaseBand(): Int {
        val baseBandVersion: String = CmdUtil.getProperty("gsm.version.baseband")
                ?: return RESULT_MAYBE_EMULATOR
        var result = RESULT_UNKNOWN
        if (baseBandVersion.contains("1.0.0.0")) {
            result = RESULT_EMULATOR
        }
        return result
    }

    /**
     * 获取传感器数量
     */
    private fun getSensorNumber(context: Context): Int {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var count = sm.getSensorList(Sensor.TYPE_ALL).size
        return count
    }

    /**
     * 获取已安装第三方应用数量
     */
    private fun getUserAppNumber(): Int {
        val userApps = CmdUtil.exec("pm list package -3")
        return getUserAppNum(userApps)
    }

    private fun getUserAppNum(userApps: String?): Int {
        if (TextUtils.isEmpty(userApps)) return 0
        val result = userApps?.split("package:".toRegex())?.toTypedArray()
        var count = result?.size ?: 0
        Loger.e(TAG,"getUserAppNum-count = $count")
        return count
    }

    /**
     * 是否支持闪光灯
     */
    private fun supportCameraFlash(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    /**
     * 是否支持相机
     */
    private fun supportCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * 是否支持蓝牙
     */
    private fun supportBluetooth(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return false为模拟器
     */
    private fun hasLightSensor(context: Context): Boolean {
        var hasLightSensor = false
        try {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) //光线传感器
            hasLightSensor = sensor != null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return hasLightSensor
    }

    /**
     * 特征参数-进程组信息
     */
    private fun checkFeaturesByCgroup(): Int {
        val filter: String? = CmdUtil.exec("cat /proc/self/cgroup")
        var result = RESULT_UNKNOWN
        if (TextUtils.isEmpty(filter)) {
            return RESULT_MAYBE_EMULATOR
        }
        return result
    }

    /**
     * 检测是否是蓝叠模拟器
     */
    fun checkBlueStacks(): Int {
        try {
            val opengl = GLES20.glGetString(GLES20.GL_RENDERER)
            if (opengl != null) {
                if (opengl.contains("Bluestacks") || opengl.contains("Translator")) {
                    return RESULT_EMULATOR
                }
            }

            val sharedFolder = File(
                Environment.getExternalStorageDirectory().toString()
                        + File.separatorChar
                        + "windows"
                        + File.separatorChar
                        + "BstSharedFolder"
            )

            if (sharedFolder.exists()) {
                return RESULT_EMULATOR
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return RESULT_UNKNOWN
    }

}