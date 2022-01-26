package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeInfo
 * Author: Victor
 * Date: 2021/4/27 19:39
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDisputeInfo {
    var complaintNo: String? = null//投诉单号
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var message: String? = null//业务消息
    var disputeType: Int = 0//争议类型：1-人才举报；2-雇主投诉
    var talentCreditAmount: Double = 0.0//人才保证金额
    var employerCreditAmount: Double = 0.0//雇主保证金额
    var prepaidAmount: Double = 0.0//已预付金额
    var status: Int = 0//业务状态：5-发起举报（投诉）；10-申请平台介入；15-平台处理完成；20-同意诉求；25-取消投诉；30-已结束
    var taskType: Int = 0//任务类型：1-工单；2-任务
}