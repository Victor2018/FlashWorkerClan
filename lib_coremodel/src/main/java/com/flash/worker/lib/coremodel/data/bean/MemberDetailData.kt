package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberDetailData
 * Author: Victor
 * Date: 2021/5/20 12:13
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberDetailData {
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//头像
    var sex: Int = 0//性别0-女；1-男；
    var age: Int = 0//年龄
    var identity: Int = 0//用户身份：1-职场人士；2-学生
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var workYears: String? = null//工作年限
    var liveCity: String? = null//现居城市(市)
    var liveDistrict: String? = null//现居城市(区)
    var talentCreditScore: Int = 0//人才信用评分
    var employerCreditScore: Int = 0//雇主信用评分
    var joinTime: String? = null//加入公会时间
}