package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserInfo
 * Author: Victor
 * Date: 2020/12/9 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
open class UserInfo {
    var userId: String? = null
    var memberId: String? = null
    var phone: String? = null
    var level: Int = 0
    var headpic: String? = null
    var sex: Int = 0//0-女；1-男；2-其它
    var username: String? = null
    var realNameStatus: Int = 0//实名认证状态：0-未认证；1-已认证
    var contactPhone: String? = null//紧急联系人电话
    var liveProvince: String? = null//现居城市(省份)
    var liveCity: String? = null//现居城市(市)
    var liveDistrict: String? = null//现居城市(区)
    var workYears: String? = null//工作年限
    var weight: Int = 0//体重
    var height: Int = 0//身高
    var identity: Int = 0//用户身份：1-职场人士；2-学生
    var talentCreditScore: Int = 0//人才信用评分
    var employerCreditScore: Int = 0//雇主信用评分
    var imAccid: String? = null
    var imToken: String? = null
    var audienceAlias: String? = null//推送别名
    var inviterUserId: String? = null//邀请人用户ID
}