package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavReleaseInfo
 * Author: Victor
 * Date: 2021/4/23 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavReleaseInfo {
    var favoriteId: String? = null//收藏ID
    var favoriteTime: String? = null//收藏时间
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var headpic: String? = null//头像
    var employerCreditScore: Int = 0//雇主信用评分
    var employerUserId: String? = null//雇主用户ID
    var username: String? = null//闪工名
    var employerId: String? = null//雇主ID
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var workDistrict: String? = null//工作地址(区)
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var paidHour: Double = 0.0//日工作时长
    var dailySalary: Double = 0.0//日薪
    var isAtHome: Boolean = false//在家可做
    var totalDays: Int = 0//总天数
    var totalAmount: Double = 0.0//报酬总价
    var settlementAmount: Double = 0.0//结算金额
    var settlementPieceCount: Int = 0//结算件数
    var status: Int = 0//发布状态：1-编辑中；2-发布中；3-已下架；4-已驳回；5-已关闭
    var type: Int = 0//发布类型：1-普通发布；2-紧急发布
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
}