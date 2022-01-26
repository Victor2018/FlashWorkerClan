package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTalentReleaseInfo
 * Author: Victor
 * Date: 2020/12/30 19:35
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTalentReleaseInfo: Serializable {
    var headpic: String? = null
    var username: String? = null
    var sex: Int = 0
    var age: Int = 0
    var talentCreditScore: Int = 0
    var height: Int = 0
    var weight: Int = 0
    var price: Double = 0.0
    var settlementMethod: Int = 0//结算方式：1-时薪；2-件薪
    var workYears: String? = null
    var highestEducation: String? = null
    var certificateNames: List<String>? = null
    var resumeId: String? = null
    var releaseId: String? = null
    var workDistrict: String? = null
    var workCity: String? = null//服务地区（市）
    var title: String? = null
    var releaseTime: String? = null
    var guildId: String? = null//公会ID
    var guildName: String? = null//公会名称
    var isAtHome: Boolean = false//远程可做
}