package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.MemberIncomeRankInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberIncomeRankReq
 * Author: Victor
 * Date: 2021/5/20 17:47
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberIncomeRankReq: BaseReq() {
    var data: List<MemberIncomeRankInfo>? = null
}