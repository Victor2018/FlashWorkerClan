package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DisputeDetailData
 * Author: Victor
 * Date: 2021/4/28 16:27
 * Description: 
 * -----------------------------------------------------------------
 */
class DisputeDetailData: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var employerName: String? = null//雇主名称
    var employerUserId: String? = null//雇主用户ID
    var employerUsername: String? = null//雇主闪工名
    var talentUserId: String? = null//人才用户ID
    var talentUsername: String? = null//人才闪工名
    var complaintNo: String? = null//投诉单号
    var disputeType: Int = 0//争议类型：1-人才举报；2-雇主投诉
    var taskType: Int = 0//任务类型：1-工单；2-任务
    var complaintItems: List<String>? = null//举报事项
    var complaintRequirements: List<String>? = null//投诉要求
    var complaintPics: List<String>? = null//相关凭证
    var disputeProgressList: List<DisputeProgressInfo>? = null//争议进度列表
    var status: Int = 0//业务状态：5-发起举报（投诉）；10-申请平台介入；15-平台处理完成；20-同意诉求；25-取消投诉；30-已结束
    var talentCreditAmount: Double = 0.0//人才保证金额
    var employerCreditAmount: Double = 0.0//雇主保证金额
    var prepaidAmount: Double = 0.0//已预付金额
    var remainCount: Int = 0//剩余次数
    var applyCompensationCreditAmount: Double = 0.0//要求赔付信用金额
    var applyCompensationPrepaidAmount: Double = 0.0//要求结算预付金额（或要求退回预付报酬金额）
}