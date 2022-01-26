package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDetailData
 * Author: Victor
 * Date: 2021/2/9 10:53
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerDetailData {
    var id: String? = null//雇主ID
    var userId: String? = null//用户ID
    var headpic: String? = null//头像
    var username: String? = null//闪工名
    var employerCreditScore: Int = 0//雇主信用评分
    var name: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licencePic: String? = null//营业执照图片
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var selfDescription: String? = null//雇主介绍
}