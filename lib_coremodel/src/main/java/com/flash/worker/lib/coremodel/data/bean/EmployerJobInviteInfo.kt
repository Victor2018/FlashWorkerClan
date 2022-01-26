package com.flash.worker.lib.coremodel.data.bean

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobInviteInfo
 * Author: Victor
 * Date: 2021/4/21 9:46
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobInviteInfo {
    var inviteId: String? = null//邀请ID
    var inviteTime: String? = null//邀请时间
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//人才简历ID
    var talentUserId: String? = null//人才用户ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var userIdentity: Int = 0//用户身份：1-职场人士；2-学生
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-整单结）
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var taskType: Int = 0//任务类型：1-工单；2-任务
    var settlementTimeLimit: Int = 0//结算时限
}