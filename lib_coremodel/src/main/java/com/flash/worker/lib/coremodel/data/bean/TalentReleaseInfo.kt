package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseInfo
 * Author: Victor
 * Date: 2020/12/25 14:27
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseInfo: Serializable {
    var id: String? = null//发布ID
    var remark: String? = null//驳回原因
    var title: String? = null//技能方向(发布标题)
    var price: Double = 0.0//报酬单价
    var settlementMethod: Int = 0//结算方式：1-时薪；2-件薪
    var inviteCount: Int = 0//收到邀请的次数
    var workDistrict: String? = null//服务地区(区)逗号分隔
    var workProvince: String? = null//服务地区(省)
    var workCity: String? = null//服务地区(市)
    var status: Int = 0//发布状态：1-编辑中；2-发布中；3-已下架；4-已驳回
    var releaseTime: String? = null//发布时间
    var expireTime: String? = null//到期时间
    var isAtHome: Boolean = false//远程可做
}