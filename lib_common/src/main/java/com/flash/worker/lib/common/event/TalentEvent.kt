package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEvent
 * Author: Victor
 * Date: 2021/7/14 17:28
 * Description: 
 * -----------------------------------------------------------------
 */
object TalentEvent {
    /**
     * 雇主发送邀请
     */
    const val employer_send_invitation = "employer_send_invitation"

    /**
     * 人才加入公会
     */
    const val talent_join_guild = "talent_join_guild"

    /**
     * 收藏人才接活
     */
    const val collect_talent_release = "collect_talent_release"

    /**
     * 查看人才全部评价
     */
    const val view_talent_all_evaluation = "view_talent_all_evaluation"

    /**
     * 检举人才违规
     */
    const val report_talent_violation = "report_talent_violation"
}