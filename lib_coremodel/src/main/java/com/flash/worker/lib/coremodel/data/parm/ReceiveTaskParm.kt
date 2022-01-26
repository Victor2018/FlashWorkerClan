package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReceiveTaskParm
 * Author: Victor
 * Date: 2021/12/8 15:40
 * Description: 
 * -----------------------------------------------------------------
 */
class ReceiveTaskParm: BaseParm() {
    var employerReleaseId: String? = null//雇主发布ID
    var resumeId: String? = null//简历ID
    var taskReceiveQty: Int = 0//领取数量
    var talentReleaseId: String? = null//人才发布ID
    var source: Int = 0//报名来源：1-直接报名；2-受邀报名
    var tradePassword: String? = null//交易密码
}