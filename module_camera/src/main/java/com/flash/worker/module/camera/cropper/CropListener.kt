package com.flash.worker.module.camera.cropper

import android.graphics.Bitmap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CropListener
 * Author: Victor
 * Date: 2020/12/18 16:49
 * Description: 
 * -----------------------------------------------------------------
 */
interface CropListener {
    fun onFinish(bitmap: Bitmap?)
}