package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployConfirmDetailReq
 * Author: Victor
 * Date: 2021/3/19 11:33
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployConfirmDetailData {
    var employmentAmount: Double = 0.0//雇用冻结金额
    var frozenRate: Double = 0.0//冻结金额比例
    var availableBalance: Double = 0.0//可用余额
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var settlementAmount: Double = 0.0//结算金额
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-整单结）
    var count: Int = 0//雇用人数
    var totalCreditFrozenAmount: Double = 0.0//信用冻结总额
    var employmentUsers: List<SettlementConfirmDetailInfo>? = null//雇用用户列表
}