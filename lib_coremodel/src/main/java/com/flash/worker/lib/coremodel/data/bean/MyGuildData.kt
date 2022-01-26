package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyGuildData
 * Author: Victor
 * Date: 2021/5/18 16:48
 * Description: 
 * -----------------------------------------------------------------
 */
class MyGuildData: Serializable {
    var guildId: String? = null//公会ID
    var guildProfile: String? = null//公会简介
    var guildName: String? = null//公会名称
    var headpic: String? = null//公会头像
    var guildProvince: String? = null//公会省份
    var guildCity: String? = null//公会城市
    var peopleNum: Int = 0//公会人数
    var memberType: Int = 0//成员类型：1-会长；2-成员
    var ownerUsername: String? = null//会长闪工名
    var ownerUserId: String? = null//会长用户ID
    var guildRules: String? = null//公会制度
    var establishTime: String? = null//公会成立时间
    var profilePics: List<String>? = null
}