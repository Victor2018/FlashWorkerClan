package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDisputeInfo
 * Author: Victor
 * Date: 2021/4/27 19:39
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerDisputeInfo {
    var complaintNo: String? = null//投诉单号
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var talentUserId: String? = null//人才用户ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var userIdentity: Int = 0//用户身份：1-职场人士；2-学生
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-整单结）
    var message: String? = null//业务消息
    var disputeType: Int = 0//争议类型：1-人才举报；2-雇主投诉
    var status: Int = 0//业务状态：5-发起举报（投诉）；10-申请平台介入；15-平台处理完成；20-同意诉求；25-取消投诉；30-已结束
    var talentCreditAmount: Double = 0.0//人才保证金额
    var employerCreditAmount: Double = 0.0//雇主保证金额
    var prepaidAmount: Double = 0.0//已预付金额
    var taskType: Int = 0//任务类型：1-工单；2-任务
}