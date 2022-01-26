package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeCertificateInfo
 * Author: Victor
 * Date: 2020/12/19 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeCertificateInfo: Serializable {
    var name: String? = null//证书名称
    var pic: String? = null//证书图片

    var index: Int = 0
}