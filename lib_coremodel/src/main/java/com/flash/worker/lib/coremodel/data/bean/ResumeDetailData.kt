package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeDetailData
 * Author: Victor
 * Date: 2020/12/22 9:34
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeDetailData: Serializable {
    var talentBaseInfo: ResumeBaseInfo? = null
    var resumeInfo: ResumeInfo? = null
    var resumeCertificates: MutableList<ResumeCertificateInfo>? = null//资质证书列表
    var resumeWorkExperiences: MutableList<ResumeWorkExperienceInfo>? = null//工作经历列表
    var resumeWorkPics: MutableList<WorkPicInfo>? = null//作品图片列表

    var modifyStatus: Boolean = false//简历修改状态：true-允许修改
    var modifyTips: String? = null//修改简历提示
}