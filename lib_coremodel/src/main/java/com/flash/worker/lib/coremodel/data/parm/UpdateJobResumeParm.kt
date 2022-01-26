package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.bean.ResumeCertificateInfo
import com.flash.worker.lib.coremodel.data.bean.ResumeInfo
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateJobResumeParm
 * Author: Victor
 * Date: 2020/12/22 11:30
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateJobResumeParm: BaseParm() {
    var resumeInfo: ResumeInfo? = null
    var resumeCertificates: List<ResumeCertificateInfo>? = null//资质证书列表
    var resumeWorkExperiences: List<ResumeWorkExperienceInfo>? = null//工作经历列表
    var resumeWorkPics: List<WorkPicInfo>? = null//作品图片列表
}