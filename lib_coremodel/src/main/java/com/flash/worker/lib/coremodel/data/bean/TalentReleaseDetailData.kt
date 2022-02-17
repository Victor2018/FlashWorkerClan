package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseDetailData
 * Author: Victor
 * Date: 2020/12/28 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseDetailData: Serializable {
    var id: String? = null//发布ID
    var title: String? = null//技能方向(发布标题)
    var price: Double = 0.0//报酬单价
    var settlementMethod: Int = 0//结算方式：1-时薪；2-件薪
    var inviteMethod: Int = 0//邀请方式：1-接受全部邀请；2-不接受低于上述单价的邀请
    var workDistrict: String? = null//服务地区(区)逗号分隔
    var workCity: String? = null//服务地区(市）
    var workProvince: String? = null//服务地区(省)
    var jobCategoryName: String? = null//人才类型
    var jobCategoryId: String? = null//人才ID
    var resumeName: String? = null//简历名称
    var resumeId: String? = null//简历ID
    var isAtHome: Boolean = false//远程可做
    var isOpenContactPhone: Boolean = false//公开联系方式
    var contactPhone: String? = null//联系电话
}