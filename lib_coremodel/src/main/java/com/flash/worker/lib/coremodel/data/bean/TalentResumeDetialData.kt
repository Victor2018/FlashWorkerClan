package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentResumeDetialData
 * Author: Victor
 * Date: 2021/2/10 14:12
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentResumeDetialData: Serializable {
    var userInfo: TalentDetailUserInfo? = null
    var resumeInfo: TalentDetailResumeInfo? = null

    var resumeId: String? = null//发送岗位邀请时使用
}