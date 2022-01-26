package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DisputeProgressInfo
 * Author: Victor
 * Date: 2021/4/28 16:31
 * Description: 
 * -----------------------------------------------------------------
 */
class DisputeProgressInfo: Serializable {
    var createTime: String? = null//创建时间
    var message: String? = null//业务消息
    var status: Int = 0//业务状态：5-发起举报（投诉）；10-申请平台介入；15-平台处理完成；20-同意诉求；25-取消投诉；30-已结束
}