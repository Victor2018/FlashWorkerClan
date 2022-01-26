package com.flash.worker.lib.coremodel.data.parm

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReleaseTaskParm
 * Author: Victor
 * Date: 2021/12/6 15:15
 * Description: 
 * -----------------------------------------------------------------
 */
class ReleaseTaskParm: BaseParm(),Serializable {
    var id: String? = null//发布ID
    var employerId: String? = null//雇主ID
    var title: String? = null//任务名称 12
    var price: Int = 0//报酬单价
    var taskQty: Int = 0//任务数量
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var workDescription: String? = null//任务描述 200
    var submitLabel: String? = null//任务提交：多个使用逗号分隔
    var pics: List<String>? = null
    var ageRequirement: String? = null//年龄要求：格式18-35;不限传空
    var sexRequirement: Int = 0//性别要求：0-女；1-男；2-男女不限
    var tradePassword: String? = null//交易密码
}