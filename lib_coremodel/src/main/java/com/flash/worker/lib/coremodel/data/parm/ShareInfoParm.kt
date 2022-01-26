package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareInfoParm
 * Author: Victor
 * Date: 2021/8/19 17:09
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareInfoParm: BaseParm() {
    var releaseId: String? = null//发布ID
    var intentType: Int = 0//进入类型
    var type: Int = 0//页面类型：1-人才详情;2-岗位详情;3,我的分享;4,工会分享
}