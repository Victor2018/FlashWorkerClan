package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UploadConfigInfo
 * Author: Victor
 * Date: 2020/12/8 19:45
 * Description: 
 * -----------------------------------------------------------------
 */
class UploadConfigInfo {
    var type: String? = null
    var endpoint: String? = null
    var bucket: String? = null
    var dir: String? = null
    var remark: String? = null
    var limit: Long = 0
}