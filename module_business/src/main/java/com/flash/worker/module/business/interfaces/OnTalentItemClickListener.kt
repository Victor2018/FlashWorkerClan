package com.flash.worker.module.business.interfaces

import com.flash.worker.lib.coremodel.data.bean.TalentCellInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnTalentItemClickListener
 * Author: Victor
 * Date: 2020/12/22 20:17
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnTalentItemClickListener {
    fun OnTalentItemClick (data: TalentCellInfo)
}