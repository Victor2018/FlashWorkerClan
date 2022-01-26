package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateGuildIntroductionParm
 * Author: Victor
 * Date: 2021/5/19 20:11
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateGuildIntroductionParm: BaseParm() {
    var guildId: String? = null//公会ID
    var guildProfile: String? = null//公会简介
    var profilePics: List<String>? = null
}