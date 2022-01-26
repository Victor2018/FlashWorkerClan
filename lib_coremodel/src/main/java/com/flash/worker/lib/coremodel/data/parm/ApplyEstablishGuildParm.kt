package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ApplyEstablishGuildParm
 * Author: Victor
 * Date: 2021/5/18 16:52
 * Description: 
 * -----------------------------------------------------------------
 */
class ApplyEstablishGuildParm: BaseParm() {
    var guildProvince: String? = null//公会省份
    var guildCity: String? = null//公会城市
    var guildName: String? = null//公会名称
    var developmentPeopleNum:Int? = null//公会人数发展承诺（1个月内）
    var memberMonthIncomeAmount: Double? = null//成员月平均收入预计
    var guildProfile: String? = null//公会简介
    var workExperience: String? = null//工作经验
    var guildRules: String? = null//公会制度
    var pics: List<String>? = null
}