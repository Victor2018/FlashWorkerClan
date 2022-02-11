package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskDetailData
 * Author: Victor
 * Date: 2021/12/7 11:53
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskDetailData: Serializable {
    var releaseId: String? = null//发布ID
    var headpic: String? = null//头像
    var employerCreditScore: Int = 0//雇主信用评分
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var employerId: String? = null//雇主ID
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var title: String? = null//发布标题
    var price: Double = 0.0//报酬单价
    var ageRequirement: String? = null//年龄要求：格式18-35;不限传空
    var sexRequirement: Int = 0//性别要求：0-女；1-男；2-男女不限
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var workDescription: String? = null//任务描述
    var submitLabel: String? = null//任务提交：多个使用逗号分隔
    var pics: List<String>? = null
    var releaseTime: String? = null//发布时间
    var requirementInfo: RequirementInfo? = null

    var status: Int = 0//发布状态：1-编辑中；2-发布中；3-已下架；4-已驳回；5，已关闭

    var isOpenContactPhone: Boolean = false//公开联系方式
    var contactPhone: String? = null//联系电话
}