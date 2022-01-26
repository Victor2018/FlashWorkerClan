package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateUserCityParm
 * Author: Victor
 * Date: 2020/12/28 14:28
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateLiveCityParm: BaseParm() {
    var liveProvince: String? = null
    var liveCity: String? = null
    var liveDistrict: String? = null
}