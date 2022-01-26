package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SaveEmployerParm
 * Author: Victor
 * Date: 2021/1/18 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class SaveEmployerParm: BaseParm() {
    var name: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licencePic: String? = null//营业执照图片
    var selfDescription: String? = null//雇主介绍
}