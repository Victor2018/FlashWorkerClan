package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.UserCouponInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponReq
 * Author: Victor
 * Date: 2021/9/28 21:33
 * Description: 
 * -----------------------------------------------------------------
 */
class UserCouponReq: BaseReq() {
    var data: ListData<UserCouponInfo>? = null
}