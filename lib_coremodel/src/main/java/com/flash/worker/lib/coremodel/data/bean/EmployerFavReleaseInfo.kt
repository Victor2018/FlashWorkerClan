package com.flash.worker.lib.coremodel.data.bean

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFavReleaseInfo
 * Author: Victor
 * Date: 2021/4/23 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFavReleaseInfo {
    var favoriteId: String? = null//收藏ID
    var favoriteTime: String? = null//收藏时间
    var talentUserId: String? = null//人才用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//头像
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var talentCreditScore: Int = 0//人才信用评分
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var workYears: String? = null//工作年限
    var highestEducation: String? = null//最高学历
    var certificateNames: List<String>? = null//证书名称
    var resumeId: String? = null//简历ID
    var talentReleaseId: String? = null//人才发布ID
    var workDistrict: String? = null//服务地区(区)逗号分隔
    var workProvince: String? = null//服务地区(省)
    var workCity: String? = null//服务地区(市)
    var title: String? = null//技能方向(发布标题)
    var price: Double = 0.0//报酬单价
    var settlementMethod: Int = 0//结算方式：1-时薪；2-整单结
    var guildId: String? = null//公会ID
    var guildName: String? = null//公会名称
    var isAtHome: Boolean = false//远程可做
}