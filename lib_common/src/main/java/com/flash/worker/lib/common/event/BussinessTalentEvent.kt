package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BussinessTalentEvent
 * Author: Victor
 * Date: 2021/7/14 17:33
 * Description: 
 * -----------------------------------------------------------------
 */
object BussinessTalentEvent {
    /**
     * 人才接活发布
     */
    const val talent_release = "talent_release"

    /**
     * 人才刷新接活
     */
    const val refresh_talent_release = "refresh_talent_release"

    /**
     * 人才再次发布接活
     */
    const val talent_release_repost = "talent_release_repost"

    /**
     * 人才删除接活
     */
    const val talent_release_delete = "talent_release_delete"

    /**
     * 人才接受邀请
     */
    const val talent_accepts_invitation = "talent_accepts_invitation"

    /**
     * 实名认证成功
     */
    const val real_name_authentication_succeeded = "real_name_authentication_succeeded"

    /**
     * 人才新建简历
     */
    const val talent_new_resume = "talent_new_resume"

    /**
     * 人才修改简历
     */
    const val talent_update_resume = "talent_update_resume"

    /**
     * 人才删除简历
     */
    const val talent_delete_resume = "talent_delete_resume"

    /**
     * 人才查看关注
     */
    const val talent_view_follow = "talent_view_follow"

    /**
     * 人才查看我的信用
     */
    const val talent_view_credit = "talent_view_credit"

    /**
     * 人才查看我的收入
     */
    const val talent_view_income = "talent_view_income"

    /**
     * 人才取消报名
     */
    const val talent_cancel_sign_up = "talent_cancel_sign_up"

    /**
     * 人才违约取消
     */
    const val talent_breach_cancel = "talent_breach_cancel"

    /**
     * 人才举报雇主
     */
    const val talent_report_employer = "talent_report_employer"

    /**
     * 人才提醒结算
     */
    const val talent_remind_settlement = "talent_remind_settlement"

    /**
     * 人才评价雇主
     */
    const val talent_evaluation_employer = "talent_evaluation_employer"

    /**
     * 人才查看评价规则
     */
    const val talent_view_evaluation_rules = "talent_view_evaluation_rules"

    /**
     * 人才删除工单
     */
    const val talent_delete_order = "talent_delete_order"

    /**
     * 人才查看评价中心
     */
    const val talent_view_evaluation_center = "talent_view_evaluation_center"

    /**
     * 人才删除评价
     */
    const val talent_delete_evaluation = "talent_delete_evaluation"

    /**
     * 人才查看投诉争议
     */
    const val talent_view_complaint_dispute = "talent_view_complaint_dispute"

    /**
     * 人才取消举报
     */
    const val talent_cancel_report = "talent_cancel_report"

    /**
     * 人才申请平台接入
     */
    const val talent_apply_platform_access = "talent_apply_platform_access"

    /**
     * 人才同意诉求
     */
    const val talent_agree_request = "talent_agree_request"
}