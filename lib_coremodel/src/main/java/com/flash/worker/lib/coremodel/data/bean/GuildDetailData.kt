package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildDetailData
 * Author: Victor
 * Date: 2021/5/19 11:39
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildDetailData: Serializable {
    var guildId: String? = null//公会ID
    var guildProfile: String? = null//公会简介
    var guildName: String? = null//公会名称
    var headpic: String? = null//公会头像
    var guildProvince: String? = null//公会省份
    var guildCity: String? = null//公会城市
    var peopleNum: Int = 0//公会人数
    var ownerUserId: String? = null//会长用户ID
    var ownerUsername: String? = null//会长闪工名
    var guildRules: String? = null//公会制度
    var profilePics: List<String>? = null
    var establishTime: String? = null//公会成立时间
    var activeGuildProfile: Boolean = false//是否能编辑公会简介
    var activeGuildRules: Boolean = false//是否能编辑公会制度
}