package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.BannerInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BannerReq
 * Author: Victor
 * Date: 2021/6/21 10:35
 * Description: 
 * -----------------------------------------------------------------
 */
class BannerReq: BaseReq() {
    var data: List<BannerInfo>? = null
}