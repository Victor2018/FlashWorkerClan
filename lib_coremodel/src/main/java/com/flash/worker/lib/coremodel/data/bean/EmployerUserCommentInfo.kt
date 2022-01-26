package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerUserCommentInfo
 * Author: Victor
 * Date: 2021/4/19 15:31
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerUserCommentInfo {
    var commentId: String? = null//评价ID
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//用户头像
    var anonymous: Boolean = false//是否匿名
    var commentTime: String? = null//评价时间
    var content: String? = null//评价内容
    var label: Int = 0//评价标签：1-好评；2-中评；3-差评
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var taskType: Int = 0//任务类型：1-工单；2-任务
}