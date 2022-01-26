package com.flash.worker.lib.coremodel.data.req


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BaseReq
 * Author: Victor
 * Date: 2020/12/7 11:51
 * Description: 
 * -----------------------------------------------------------------
 */
open class BaseReq {
    var code: String? = null//ok 成功，否则失败
    var message: String? = null//请求失败的错误信息
    var traceId: String? = null
}