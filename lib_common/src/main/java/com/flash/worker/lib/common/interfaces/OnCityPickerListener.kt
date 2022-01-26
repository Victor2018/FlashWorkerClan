package com.flash.worker.lib.common.interfaces

import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import com.flash.worker.lib.coremodel.data.bean.CityInfo
import com.flash.worker.lib.coremodel.data.bean.ProvinceInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnCityPickerListener
 * Author: Victor
 * Date: 2020/12/17 18:15
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnCityPickerListener {
    fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?)
}