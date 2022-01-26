package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSignUpUserInfo
 * Author: Victor
 * Date: 2021/2/9 15:04
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSignUpUserInfo {
    var id: String? = null
    var employerReleaseId: String? = null//雇主发布ID
    var signupTime: String? = null//报名时间
    var talentUserId: String? = null//人才用户ID
    var talentReleaseId: String? = null//简历ID
    var resumeId: String? = null//人才发布ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var source: Int = 0//报名来源：1-人才报名；2-雇主邀请
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var userIdentity: Int = 0//用户身份：1-职场人士；2-学生
}