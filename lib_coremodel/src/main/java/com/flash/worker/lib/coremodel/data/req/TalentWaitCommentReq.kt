package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitCommentReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitCommentReq: BaseReq() {
    var data: ListData<TalentWaitCommentInfo>? = null
}