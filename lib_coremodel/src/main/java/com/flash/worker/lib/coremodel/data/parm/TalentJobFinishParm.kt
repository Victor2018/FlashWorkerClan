package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobFinishParm
 * Author: Victor
 * Date: 2021/2/8 16:45
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobFinishParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var type: Int? = null//业务分类：10-完成工作；20-解约工作；30-取消报名；全部传空


}