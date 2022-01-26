package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseApi
 * Author: Victor
 * Date: 2021/4/12 20:08
 * Description: 
 * -----------------------------------------------------------------
 */
object EmployerReleaseApi {
    const val SAVE_EMPLOYER_DRAFTS = "employerRelease/saveDrafts"
    const val SAVE_EMPLOYER_RELEASE = "employerRelease/saveRelease"
    const val UPDATE_EMPLOYER_DRAFTS = "employerRelease/updateDrafts"
    const val UPDATE_EMPLOYER_RELEASE = "employerRelease/updateRelease"
    const val EMPLOYER_RELEASE_LIST = "employerRelease/listByUser"
    const val EMPLOYER_RELEASE_REFRESH = "employerRelease/refresh"
    const val EMPLOYER_RELEASE_OFFSHELF = "employerRelease/offRelease"
    const val EMPLOYER_RELEASE_NEW_RELEASE = "employerRelease/fastRelease"
    const val EMPLOYER_RELEASE_DELETE = "employerRelease/delete"
    const val EMPLOYER_RELEASE_DETAIL = "employerRelease/getDetail"
    const val INVITE_TALENT_EMPLOYER_RELEASE = "employerRelease/inviteTalentEmployerReleaseList"
}