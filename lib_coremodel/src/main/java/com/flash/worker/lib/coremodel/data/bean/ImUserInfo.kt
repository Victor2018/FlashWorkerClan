package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ImUserInfo
 * Author: Victor
 * Date: 2021/10/22 10:33
 * Description: 
 * -----------------------------------------------------------------
 */
class ImUserInfo {
    var userId: String? = null//用户ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var weight: Int = 0//体重
    var height: Int = 0//身高
    var identity: Int = 0//身高
    var workYears: String? = null//工作年限
    var liveProvince: String? = null//现居城市(省)
    var liveCity: String? = null//现居城市(市)
    var liveDistrict: String? = null//现居城市(区)
    var talentCreditScore: Int = 0//人才信用评分
    var employerCreditScore: Int = 0//雇主信用评分
}