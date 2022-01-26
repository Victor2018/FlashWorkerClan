package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTalentReleaseParm
 * Author: Victor
 * Date: 2020/12/30 19:13
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchEmployerReleaseParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int? = null//页编号
    var workCity: String? = null//工作城市
    var keywords: String? = null//关键词
    var jobCategoryId: String? = null//人才类型ID
    var minPrice: Int? = null//最小报酬
    var maxPrice: Int? = null//最大报酬
    var workDistrict: String? = null//服务地区
    var sortType: String? = null//排序方式：creditDesc-信用降序；jobStartTimeAsc-开工日期(由近到远)；
    // priceDesc-单价降序；priceAsc-单价升序

    var sex: Int? = null//性别（0-女；1-男；2-其它）
    var identity: Int? = null//雇主类型：1-企业；2-商户；3-个人
    var type: Int? = null//发布类型：1-普通发布；2-紧急发布
    var settlementMethod: Int? = null//结算方式（1-日结；2-周结；3-件结）
    var isAtHome: Boolean? = null//在家可做
    var identityRequirement: Int? = null//身份要求：1-职场人士；2-学生；3-不限

}