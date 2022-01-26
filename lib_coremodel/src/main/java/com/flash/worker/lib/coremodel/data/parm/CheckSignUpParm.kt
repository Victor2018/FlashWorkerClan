package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckSignUpParm
 * Author: Victor
 * Date: 2021/2/7 14:25
 * Description: 
 * -----------------------------------------------------------------
 */
class CheckSignUpParm: BaseParm() {
    var resumeId: String? = null//简历ID
    var employerReleaseId: String? = null//发布ID
    var source: Int = 0//报名来源：1-直接报名；2-受邀报名
    var talentReleaseId: String? = null//人才发布ID-报名来源为2时必须传
}