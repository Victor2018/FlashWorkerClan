package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DisputeHistoryInfo
 * Author: Victor
 * Date: 2022/1/13 14:18
 * Description: 
 * -----------------------------------------------------------------
 */

class DisputeHistoryInfo {
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
    var complaintItems: List<String>? =null
    var complaintRequirements: List<String>? =null
    var complaintPics: List<String>? =null
    var createTime: String? = null//创建时间

    var categoryPosition: Int = 0//增加父list下标方便点击返回使用
}