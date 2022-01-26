package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AreaTreeData
 * Author: Victor
 * Date: 2020/12/17 16:23
 * Description: 
 * -----------------------------------------------------------------
 */
class ProvinceInfo: Serializable {
    var id: Int = 0
    var parentId: Int = 0
    var name: String? = null
    var childs: List<CityInfo>? = null
}