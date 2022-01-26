package com.flash.worker.module.business.interfaces

import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnRewardMatterSelectListener
 * Author: Victor
 * Date: 2021/3/23 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnRewardLabelSelectListener {
    fun OnRewardLabelSelect(data: RewardLabelInfo?)
}