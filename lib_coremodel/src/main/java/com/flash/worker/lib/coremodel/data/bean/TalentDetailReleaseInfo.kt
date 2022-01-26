package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDetailReleaseInfo
 * Author: Victor
 * Date: 2021/1/4 14:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDetailReleaseInfo: Serializable {
    var releaseId: String? = null
    var title: String? = null
    var price: Double = 0.0
    var settlementMethod: Int = 0
    var workDistrict: String? = null
    var workCity: String? = null
    var status: Int = 0//发布状态：1-编辑中；2-发布中；3-已下架；4-已驳回；
    var isAtHome: Boolean = false//远程可做
}