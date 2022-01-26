package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDetailUserInfo
 * Author: Victor
 * Date: 2021/1/4 14:35
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDetailUserInfo: Serializable {
    var headpic: String? = null
    var username: String? = null
    var workYears: String? = null
    var liveCity: String? = null
    var liveDistrict: String? = null
    var talentCredit: Int = 0
    var sex: Int = 0
    var age: Int = 0
    var height: Int = 0
    var weight: Int = 0
    var identity: Int = 0
    var userId: String? = null
    var talentCreditScore: Int = 0
}