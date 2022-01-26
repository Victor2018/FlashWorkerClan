package com.flash.worker.lib.common.data

import com.luck.picture.lib.entity.LocalMedia


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UploadData
 * Author: Victor
 * Date: 2020/12/9 10:32
 * Description: 
 * -----------------------------------------------------------------
 */
class UploadData {
    var localMedia: LocalMedia? = null

    var dir: String? = null
    var bucketName: String? = null
    var stsServerUrl: String? = null
    var ossEndPoint: String? = null
}