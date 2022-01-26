package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCellInfo
 * Author: Victor
 * Date: 2020/12/22 19:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCellInfo {
    var id: String? = null
    var parentId: String? = null
    var name: String? = null
    var childs: List<TalentCellInfo>? = null
}