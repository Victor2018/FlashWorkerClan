package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MenuInfo
 * Author: Victor
 * Date: 2020/12/2 15:22
 * Description: 
 * -----------------------------------------------------------------
 */
class MenuInfo {
    var icon: String? = null
    var menuName: String? = null
    var style: String? = null
    var type: String? = null
    var poster: String? = null
    var sort = 0
    var menuId = 0
    var categoryId = 0
    var recommend: List<RecommendInfo>? = null
}