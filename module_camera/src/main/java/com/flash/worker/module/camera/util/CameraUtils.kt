package com.flash.worker.module.camera.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CameraUtils
 * Author: Victor
 * Date: 2020/12/18 15:31
 * Description: 
 * -----------------------------------------------------------------
 */
object CameraUtils {
    private var camera: Camera? = null

    /**
     * 检查是否有相机
     *
     * @param context
     * @return
     */
    fun hasCamera(context: Context): Boolean {
        return context.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * 打开相机
     *
     * @return
     */
    fun openCamera(): Camera? {
        camera = null
        try {
            camera = Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }
        return camera // returns null if camera is unavailable
    }

    fun getCamera(): Camera? {
        return camera
    }

    /**
     * 检查是否有闪光灯
     *
     * @return true：有，false：无
     */
    fun hasFlash(context: Context): Boolean {
        return context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
}