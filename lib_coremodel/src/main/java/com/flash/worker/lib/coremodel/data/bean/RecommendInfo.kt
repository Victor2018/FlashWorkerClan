package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecommendInfo
 * Author: Victor
 * Date: 2020/12/2 15:23
 * Description: 
 * -----------------------------------------------------------------
 */
class RecommendInfo {
    var isDisplayTitle: String? = null
    var description: String? = null
    var style: String? = null
    var title: String? = null
    var type: String? = null
    var poster: String? = null
    var url: String? = null
    var maxDisplayCount = 0
    var sort = 0

    var rContent: List<ContentInfo>? = null
}