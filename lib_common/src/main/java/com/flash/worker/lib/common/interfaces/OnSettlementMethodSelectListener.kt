package com.flash.worker.lib.common.interfaces

import com.flash.worker.lib.common.data.SettlementInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnSettlementMethodSelectListener
 * Author: Victor
 * Date: 2021/1/6 11:57
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnSettlementMethodSelectListener {
    fun OnSettlementMethodSelect (settlementMethod : SettlementInfo?)
}