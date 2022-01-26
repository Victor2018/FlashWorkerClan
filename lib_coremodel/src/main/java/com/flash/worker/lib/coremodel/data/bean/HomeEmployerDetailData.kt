package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HomeEmployerDetailData
 * Author: Victor
 * Date: 2021/1/22 9:27
 * Description: 
 * -----------------------------------------------------------------
 */
class HomeEmployerDetailData: Serializable {
    var id: String? = null//发布ID
    var headpic: String? = null//头像
    var username: String? = null//闪工名
    var userId: String? = null//闪工ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var jobStartTime: String? = null//工作开始时间
    var jobEndTime: String? = null//工作结束时间
    var workDescription: String? = null//工作描述
    var workProvince: String? = null//工作地址(省)
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var address: String? = null//详细地址
    var startTime: String? = null//日开工时间
    var endTime: String? = null//日完工时间
    var ageRequirement: String? = null//年龄要求：格式18-35;不限传空
    var eduRequirement: String? = null//学历要求：不限传空
    var deadline: String? = null//录取截止时间
    var employerCreditScore:Int = 0//信用分
    var identity:Int = 0//雇主类型：1-企业；2-商户；3-个人
    var peopleCount:Int = 0//雇佣人数
    var settlementMethod:Int = 0//结算方式（1-日结；2-周结；3-件结）
    var payrollMethod:Int = 0//记薪方式：1-时薪；2-件薪
    var sexRequirement:Int = 0//性别要求：0-女；1-男；2-男女不限
    var type:Int = 0//发布类型：1-普通发布；2-紧急发布
    var totalDays:Int = 0//总天数
    var settlementPieceCount:Int = 0//当期结算件数
    var paidHour:Double = 0.0//日记薪小时
    var price: Double = 0.0//报酬单价
    var dailySalary: Double = 0.0//日薪
    var totalAmount: Double = 0.0//总金额
    var settlementAmount: Double = 0.0//当期结算金额
    var countDownTime: Long = 0//倒计时 ms
    var status: Int = 0//发布状态：1-编辑中；2-发布中；3-已下架；4-已驳回；5，已关闭
    var licenceAuth: Boolean = false//企业是否认证
    var employerId: String? = null//雇主ID
    var pics: List<String>? = null
    var isAtHome:Boolean = false//在家可做
    var identityRequirement: Int = 0//身份要求：1-职场人士；2-学生；3-不限
    var checkSignup: CheckSignUpData? = null
    var favoriteStatus: Boolean = false//收藏状态：true-已收藏

}