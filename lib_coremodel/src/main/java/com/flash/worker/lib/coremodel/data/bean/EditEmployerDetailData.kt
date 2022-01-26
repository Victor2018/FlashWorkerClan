package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDetailData
 * Author: Victor
 * Date: 2021/1/18 18:40
 * Description: 
 * -----------------------------------------------------------------
 */
class EditEmployerDetailData {
    var id: String? = null//雇主ID
    var userId: String? = null//用户ID
    var name: String? = null//雇主名称
    var licencePic: String? = null
    var selfDescription: String? = null//雇主介绍
    var workProvince: String? = null//工作地址(省)
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var address: String? = null//详细地址
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
}
