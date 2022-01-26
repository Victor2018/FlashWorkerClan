package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentCommentCenterInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCommentCenterReq
 * Author: Victor
 * Date: 2021/4/13 16:43
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCommentCenterReq: BaseReq() {
    var data: ListData<TalentCommentCenterInfo>? = null
}