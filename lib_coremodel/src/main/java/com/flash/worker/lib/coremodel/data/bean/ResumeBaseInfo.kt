package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeBaseInfo
 * Author: Victor
 * Date: 2020/12/19 10:33
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeBaseInfo: Serializable {
    var username: String? = null//闪工名
    var liveProvince: String? = null//现居城市(省份)
    var liveCity: String? = null//现居城市(市)
    var liveDistrict: String? = null//现居城市(区)
    var identity: Int = 0//用户身份：1-职场人士；2-学生
    var height: Int = 0//身高 单位cm
    var weight: Int = 0//体重 单位kg
    var workYears: String? = null//工作年限
    var headpic: String? = null//头像

}