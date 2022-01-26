package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentUserInfo
 * Author: Victor
 * Date: 2021/4/8 14:38
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentUserInfo: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var employerReleaseId: String? = null//雇主发布ID
    var finishTime: String? = null//结束时间
    var finishType: Int = 0//结束类型：1-人才解约；2-雇主解约；3-完工结束
    var receivedCompensationAmount: Double = 0.0//获赔金额
    var compensationAmount: Double = 0.0//赔付金额
    var totalSettledAmount: Double = 0.0//结算合计
    var totalRewardAmount: Double = 0.0//奖励合计
    var talentUserId: String? = null//人才用户ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//简历ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var userIdentity: Int = 0//用户身份：1-职场人士；2-学生
}