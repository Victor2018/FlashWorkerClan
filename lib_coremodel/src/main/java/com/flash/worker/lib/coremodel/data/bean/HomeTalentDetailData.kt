package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDetailData
 * Author: Victor
 * Date: 2021/1/4 14:34
 * Description: 
 * -----------------------------------------------------------------
 */
class HomeTalentDetailData: Serializable {
    var userInfo: TalentDetailUserInfo? = null
    var talentReleaseInfo: TalentDetailReleaseInfo? = null
    var resumeInfo: TalentDetailResumeInfo? = null
    var checkSendInviteInfo: CheckSendInviteInfo? = null
    var favoriteStatus: Boolean = false//收藏状态：true-已收藏
}