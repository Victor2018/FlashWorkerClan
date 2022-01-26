package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BussinessEmployerEvent
 * Author: Victor
 * Date: 2021/7/14 17:36
 * Description: 
 * -----------------------------------------------------------------
 */
object BussinessEmployerEvent {
    /**
     * 雇主发布雇用
     */
    const val employment_release = "employment_release"

    /**
     * 雇主刷新雇用
     */
    const val refresh_employment_release = "refresh_employment_release"

    /**
     * 雇主刷新雇用
     */
    const val off_shelf_employment_release = "off_shelf_employment_release"

    /**
     * 雇主再次发布雇用
     */
    const val employment_release_repost = "employment_release_repost"

    /**
     * 雇主删除雇用
     */
    const val employer_delete_employment = "employer_delete_employment"

    /**
     * 雇主查看已发邀请
     */
    const val employer_view_invitation = "employer_view_invitation"

    /**
     * 新增雇主
     */
    const val new_employer = "new_employer"

    /**
     * 更新雇主
     */
    const val update_employer = "update_employer"

    /**
     * 删除雇主
     */
    const val delete_employer = "delete_employer"

    /**
     * 雇主查看人才收藏
     */
    const val employer_view_talent_collection = "employer_view_talent_collection"

    /**
     * 雇主查看信用
     */
    const val employer_view_credit = "employer_view_credit"

    /**
     * 雇主查看报酬支出
     */
    const val employer_view_expense = "employer_view_expense"

    /**
     * 雇主终止雇用
     */
    const val employer_termination_employment = "employer_termination_employment"

    /**
     * 雇主投诉人才
     */
    const val employer_complaint_talent = "employer_complaint_talent"

    /**
     * 雇主奖励人才
     */
    const val employer_reward_talent = "employer_reward_talent"

    /**
     * 雇主违约解雇
     */
    const val employer_breach_cancel = "employer_breach_cancel"

    /**
     * 雇主删除工单
     */
    const val employer_delete_order = "employer_delete_order"

    /**
     * 雇主查看评价中心
     */
    const val employer_view_evaluation_center = "employer_view_evaluation_center"

    /**
     * 雇主删除评价
     */
    const val employer_delete_evaluation = "employer_delete_evaluation"

    /**
     * 雇主查看投诉处理
     */
    const val employer_view_complaint_dispute = "employer_view_complaint_dispute"

    /**
     * 雇主取消投诉
     */
    const val employer_cancel_complaint = "employer_cancel_complaint"

    /**
     * 雇主申请平台接入
     */
    const val employer_apply_platform_access = "employer_cancel_complaint"

    /**
     * 雇主同意诉求
     */
    const val employer_agree_request = "employer_cancel_complaint"
}