package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDetailResumeInfo
 * Author: Victor
 * Date: 2021/1/4 14:40
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDetailResumeInfo: Serializable {
    var highestEducation: String? = null
    var selfDescription: String? = null
    var resumeWorkExperiences: List<ResumeWorkExperienceInfo>? = null
    var resumeCertificates: List<ResumeCertificateInfo>? = null
    var resumeWorkPics: List<WorkPicInfo>? = null
}