package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MineEvent
 * Author: Victor
 * Date: 2021/7/14 17:40
 * Description: 
 * -----------------------------------------------------------------
 */
object MineEvent {
    /**
     * 修改头像
     */
    const val edit_avatar = "edit_avatar"

    /**
     * 修改闪工名
     */
    const val edit_name = "edit_name"

    /**
     * 修改工作年限
     */
    const val edit_work_years = "edit_work_years"

    /**
     * 修改身份
     */
    const val edit_identity = "edit_identity"

    /**
     * 修改身高
     */
    const val edit_height = "edit_height"

    /**
     * 修改体重
     */
    const val edit_weight = "edit_weight"

    /**
     * 修改现居城市
     */
    const val edit_live_city = "edit_live_city"

    /**
     * 修改紧急联系人
     */
    const val edit_emergency_contact = "edit_emergency_contact"

    /**
     * 微信充值
     */
    const val recharge_from_wechat = "recharge_from_wechat"

    /**
     * 支付宝充值
     */
    const val recharge_from_zfb = "recharge_from_zfb"

    /**
     * 提现到支付宝
     */
    const val withdraw_to_zfb = "withdraw_to_zfb"

    /**
     * 查看人才红包
     */
    const val view_red_envelope = "view_red_envelope"

    /**
     * 领取红包
     */
    const val receive_red_envelope = "receive_red_envelope"

    /**
     * 查看雇用奖励
     */
    const val view_employment_reward = "view_employment_reward"

    /**
     * 领取奖励
     */
    const val receive_employment_reward = "receive_employment_reward"

    /**
     * 查看公会资讯
     */
    const val view_guild_news = "view_guild_news"

    /**
     * 查看公会收入月榜
     */
    const val view_guild_monthly_income = "view_guild_monthly_income"

    /**
     * 资讯发布
     */
    const val new_guild_news = "new_guild_news"

    /**
     * 修改公会头像
     */
    const val edit_guild_avatar = "edit_guild_avatar"

    /**
     * 修改公会简介
     */
    const val edit_guild_introduction = "edit_guild_introduction"

    /**
     * 修改公会制度
     */
    const val edit_guild_rules = "edit_guild_rules"

    /**
     * 查看成员收入统计
     */
    const val view_member_income_statistics = "view_member_income_statistics"

    /**
     * 查看管理须知
     */
    const val view_mamagement_notes = "view_mamagement_notes"

    /**
     * 进入公会大厅
     */
    const val enter_guild_hall = "enter_guild_hall"

    /**
     * 查看操作指引
     */
    const val view_operation_guide = "view_operation_guide"

    /**
     * 领取公会红包
     */
    const val receive_guild_red_envelope = "receive_guild_red_envelope"

    /**
     * 修改交易密码
     */
    const val edit_transaction_password = "edit_transaction_password"

    /**
     * 重置交易密码
     */
    const val reset_transaction_password = "reset_transaction_password"

    /**
     * 绑定支付宝
     */
    const val bind_zhb = "bind_zhb"

    /**
     * 解绑支付宝
     */
    const val unbind_zfb = "unbind_zfb"

    /**
     * 注销账户
     */
    const val cancel_account = "cancel_account"

    /**
     * 查看服务协议
     */
    const val view_service_agreement = "view_service_agreement"

    /**
     * 查看隐私政策
     */
    const val view_privacy_policy = "view_privacy_policy"

    /**
     * 清除缓存
     */
    const val clear_cache = "clear_cache"

    /**
     * 退出登录
     */
    const val logout = "logout"

    /**
     * 查看违规处理公告
     */
    const val view_violation_notice = "view_violation_notice"
}