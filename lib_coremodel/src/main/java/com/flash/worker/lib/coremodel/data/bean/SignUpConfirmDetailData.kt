package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SignUpConfirmDetailData
 * Author: Victor
 * Date: 2021/2/7 14:55
 * Description: 
 * -----------------------------------------------------------------
 */
class SignUpConfirmDetailData {
    var signupAmount:Double = 0.0//报名金额
    var frozenRate: Double = 0.0//冻结金额比例
    var availableBalance:Double = 0.0//可用余额
    var settlementAmount:Double = 0.0//结算金额
    var title:String? = null//发布标题
    var employerName:String? = null//雇主名称
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
}