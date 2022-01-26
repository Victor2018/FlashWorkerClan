package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentLastCommentInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCommentReq
 * Author: Victor
 * Date: 2021/4/15 14:19
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCommentReq: BaseReq() {
    var data: ListData<TalentLastCommentInfo>? = null
}