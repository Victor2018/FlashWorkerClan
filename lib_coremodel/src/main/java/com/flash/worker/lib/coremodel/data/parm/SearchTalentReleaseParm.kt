package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
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
class SearchTalentReleaseParm: BaseParm() {
    var keywords: String? = null//关键词
    var workCity: String? = null//工作城市
    var jobCategoryId: String? = null//人才类型ID
    var workDistrict: String? = null//服务地区
    var pageNum: Int? = null//页编号
    var sex: Int? = null//性别（0-女；1-男；2-其它）
    var minAge: Int? = null//最小年龄
    var maxAge: Int? = null//最大年龄
    var minPrice: Int? = null//最小报酬
    var maxPrice: Int? = null//最大报酬
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var sortType: String? = null//排序方式：creditDesc-信用降序；ageDesc-年龄降序；
    // ageAsc-年龄升序；priceDesc-单价降序；priceAsc-单价升序


}