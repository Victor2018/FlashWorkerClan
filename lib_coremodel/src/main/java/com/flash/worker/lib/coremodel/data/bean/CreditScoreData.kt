package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditScoreData
 * Author: Victor
 * Date: 2021/4/22 17:10
 * Description: 
 * -----------------------------------------------------------------
 */
class CreditScoreData {
    var goodCommentNum: Int = 0//获得好评次数
    var generalCommentNum: Int = 0//获得中评次数
    var badCommentNum: Int = 0//获得差评次数
    var totalCommentNum: Int = 0//获得评价总次数
    var creditScore: Int = 0//信用评分
}