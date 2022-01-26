package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentNumData
 * Author: Victor
 * Date: 2021/3/19 17:50
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentNumData {
    var employerReleaseId: String? = null//雇主发布ID
    var employmentNum:Int = 0//拟雇用人数
    var realEmploymentNum:Int = 0//实际已雇用人数
    var signupNum:Int = 0//已报名人数
    var submitNum:Int = 0//已提交人数
    var receiveNum:Int = 0//已领取人数
    var cancelContractNum:Int = 0//已解约人数
    var settledNum:Int = 0//已结算人数(正常完工的人)
    var receivedCompensationNum:Int = 0//获赔人数
    var receivedCompensationAmount:Double = 0.0//获赔金额
    var compensationNum:Int = 0//赔付人数
    var compensationAmount:Double = 0.0//赔付金额
}