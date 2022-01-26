package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildApplyRecordInfo
 * Author: Victor
 * Date: 2021/5/18 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildApplyRecordInfo {
    var uildApplyId: String? = null//公会申请ID
    var guildProvince: String? = null//公会省份
    var guildCity: String? = null//公会城市
    var guildName: String? = null//公会名称
    var applyTime: String? = null//申请时间
    var rejectTime: String? = null//驳回时间
    var auditTime: String? = null//审核通过时间
    var remark: String? = null//审批备注
    var status: Int = 0//业务状态：1-审核中；2-已驳回；3-已通过
}