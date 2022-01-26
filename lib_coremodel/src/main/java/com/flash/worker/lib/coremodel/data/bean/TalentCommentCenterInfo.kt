package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCommentCenterInfo
 * Author: Victor
 * Date: 2021/4/13 16:44
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCommentCenterInfo {
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作开始日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var talentComment: TalentCommentInfo? = null//人才评价
    var employerComment: EmployerCommentInfo? = null//雇主评价
    var taskType: Int = 0//任务类型：1-工单；2-任务
}