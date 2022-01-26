package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildMemberInfo
 * Author: Victor
 * Date: 2021/5/20 12:10
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildMemberInfo: Serializable {
    var guildId: String? = null//公会ID
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//头像
    var sex: Int = 0//性别0-女；1-男；
}