package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SaveTalentJobParm
 * Author: Victor
 * Date: 2020/12/23 15:36
 * Description: 
 * -----------------------------------------------------------------
 */
class SaveTalentReleaseParm: BaseParm() {
    var inviteMethod: Int? = null//邀请方式：1-接受全部邀请；2-不接受低于上述单价的邀请
    var jobCategoryId: String? = null//人才类型ID
    var price: Double? = null//报酬单价
    var resumeId: String? = null//简历ID
    var settlementMethod: Int? = null//结算方式：1-时薪；2-件薪
    var title: String? = null//发布标题
    var workProvince: String? = null//服务地区（省）
    var workCity: String? = null//服务地区（市）
    var workDistrict: String? = null//服务地区（区）逗号分隔
    var isAtHome: Boolean = false//远程可做
    var isOpenContactPhone: Boolean = false//公开联系方式
    var contactPhone: String? = null//联系电话
}