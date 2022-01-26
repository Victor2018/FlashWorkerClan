package com.flash.worker.module.business.interfaces

import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnSettlementDateSelectListener
 * Author: Victor
 * Date: 2021/3/22 10:28
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnSettlementDateSelectListener {
    fun OnSettlementDate(data: SettlementDateInfo?)
}