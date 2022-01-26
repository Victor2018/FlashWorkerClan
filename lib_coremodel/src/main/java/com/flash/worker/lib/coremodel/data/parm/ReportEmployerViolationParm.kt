package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportEmployerViolationParm
 * Author: Victor
 * Date: 2021/7/15 17:31
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportEmployerViolationParm: BaseParm() {
    var labelId: String? = null//违规标签ID
    var employerReleaseId: String? = null//人才发布ID
    var reportDesc: String? = null//具体情况说明
    var pics: List<String>? = null//相关凭证图片
}