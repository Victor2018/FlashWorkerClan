package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.CustomerServiceInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomerServiceReq
 * Author: Victor
 * Date: 2021/6/4 12:10
 * Description: 
 * -----------------------------------------------------------------
 */
class CustomerServiceReq: BaseReq() {
    var data: List<CustomerServiceInfo>? = null
}