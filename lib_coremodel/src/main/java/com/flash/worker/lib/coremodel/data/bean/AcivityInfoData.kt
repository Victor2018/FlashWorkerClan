package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AcivityInfoData
 * Author: Victor
 * Date: 2021/5/31 17:15
 * Description: 
 * -----------------------------------------------------------------
 */
class AcivityInfoData {
    var activityId: String? = null//活动ID
    var activityTitle: String? = null//活动标题
    var activityStartTime: String? = null//活动开始时间
    var activityEndTime: String? = null//活动结束时间
    var activityRulesUrl: String? = null//活动规则URL
    var totalReceiveCount: Int = 0//累计已领取个数
    var totalReceiveAmount: Double = 0.0//累计已领取金额
    var receiveInfos: List<AcivityInfo>? = null
}