package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommentEmployerParm
 * Author: Victor
 * Date: 2021/4/8 12:20
 * Description: 
 * -----------------------------------------------------------------
 */
class CommentEmployerParm: BaseParm() {
    var anonymous: Boolean = false//是否匿名: true-匿名
    var label: Int = 0//评价标签：1-很好；2-一般；3-很差
    var content: String? = null//评价内容
    var employerReleaseId: String? = null//雇主发布ID
    var jobOrderId: String? = null//工单ID
}