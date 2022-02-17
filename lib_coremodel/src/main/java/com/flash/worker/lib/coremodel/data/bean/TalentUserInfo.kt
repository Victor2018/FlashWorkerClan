package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentUserInfo
 * Author: Victor
 * Date: 2021/3/18 16:34
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentUserInfo: Serializable {
    var id: String? = null//ID
    var employerReleaseId: String? = null//雇主发布ID
    var signupTime: String? = null//报名时间
    var cancelSignupTime: String? = null//取消报名时间
    var employmentTime: String? = null//录取时间
    var talentUserId: String? = null//人才用户ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//简历ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var source: Int = 0//报名来源：1-人才报名；2-雇主邀请
    var sex: Int = 0// 性别（0-女；1-男；2-其它）
    var age: Int = 0// 年龄
    var userIdentity: Int = 0// 用户身份：1-职场人士；2-学生
    var cancelSignupType: Int = 0//取消报名类型：1-人才取消；2-系统取消；3-雇主拒绝
    var signupFrozenAmount: Double = 0.0//报名冻结金额
    var guildId: String? = null//公会ID
    var guildName: String? = null//公会名称
}