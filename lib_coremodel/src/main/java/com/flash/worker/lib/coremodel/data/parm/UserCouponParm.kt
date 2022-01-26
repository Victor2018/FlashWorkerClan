package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponParm
 * Author: Victor
 * Date: 2021/9/28 21:32
 * Description: 
 * -----------------------------------------------------------------
 */
class UserCouponParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var type: Int? = 0//类型：10-待使用列表；20-历史列表
}