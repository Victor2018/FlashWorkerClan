package com.flash.worker.lib.luban

import android.graphics.Bitmap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompressConfig
 * Author: Victor
 * Date: 2020/12/23 19:15
 * Description: 
 * -----------------------------------------------------------------
 */
class CompressConfig {
    var grade = 0

    /**
     * 压缩到的最大大小，单位B
     */
    var maxSize = 100 * 1024 / 1000
    var maxWidth = 0
    var maxHeight = 0
    var compressFormat = Bitmap.CompressFormat.JPEG
}