package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementDetailData
 * Author: Victor
 * Date: 2021/12/9 11:44
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementDetailData: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var settlementOrderId: String? = null//结算工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var employerUserId: String? = null//雇主用户ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var price: Double = 0.0//报酬单价
    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var prepaidAmount: Double = 0.0//已预付金额
    var finishDeadline: String? = null//任务完成期限
    var finishTime: String? = null//任务提交时间
    var employerUsername:String? = null//雇主闪工名
    var submitLabel: String? = null//任务提交：多个使用逗号分隔
}