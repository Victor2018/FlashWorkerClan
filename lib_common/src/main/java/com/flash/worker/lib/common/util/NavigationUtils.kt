package com.flash.worker.lib.common.util

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.data.IDCardType
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementConfirmDetailParm
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NavigationUtils
 * Author: Victor
 * Date: 2020/7/4 下午 04:09
 * Description: ARouter路由跳转工具类
 * -----------------------------------------------------------------
 */
object NavigationUtils {
    val TITLE_KEY = "TITLE_KEY"
    val TYPE_KEY = "TYPE_KEY"
    val PHONE_KEY = "PHONE_KEY"
    val CODE_KEY = "CODE_KEY"
    val POSITION_KEY = "POSITION_KEY"
    val ID_KEY = "ID_KEY"
    val AUTH_SECRET_KEY = "AUTH_SECRET_KEY"
    val ONE_KEY_LOGIN_ENVAVAIABLE_KEY = "ONE_KEY_LOGIN_ENVAVAIABLE_KEY"

    /**
     * 去往首页
     */
    fun goHomeActivity(activity: Activity,pagerPosition: Int,pushData: String? = null) {
        ARouter.getInstance().build(ARouterPath.MainAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,pushData)
            .withInt(Constant.POSITION_KEY,pagerPosition)
            .navigation(activity)
    }

    /**
     * 去往验证码登录页面
     */
    fun goCodeLoginActivity(activity: Activity?,oneKeyLoginEnvAvailable: Boolean) {
        if (activity == null) {
            ARouter.getInstance().build(ARouterPath.CodeLoginAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .withBoolean(ONE_KEY_LOGIN_ENVAVAIABLE_KEY,oneKeyLoginEnvAvailable)
                .navigation()
        } else {
            ARouter.getInstance().build(ARouterPath.CodeLoginAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .withBoolean(ONE_KEY_LOGIN_ENVAVAIABLE_KEY,oneKeyLoginEnvAvailable)
                .navigation(activity)
        }
    }
    /**
     * 去往绑定手机号页面
     */
    fun goBindPhoneActivity(activity: Activity,openId: String?) {
        ARouter.getInstance().build(ARouterPath.BindPhoneAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,openId)
            .navigation(activity)
    }
    /**
     * 去往账号被冻结页面
     */
    fun goAccountFrozenActivity(activity: Activity,msg: String?) {
        ARouter.getInstance().build(ARouterPath.AccountFrozenAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,msg)
            .navigation(activity)
    }
    /**
     * 去往账号被注销页面
     */
    fun goAccountCancelledActivity(activity: Activity,msg: String?) {
        ARouter.getInstance().build(ARouterPath.AccountCancelledAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,msg)
            .navigation(activity)
    }
    /**
     * 去往绑定手机号页面
     */
    fun goCameraActivity(activity: Activity,takeType: Int) {
        ARouter.getInstance().build(ARouterPath.CameraAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withInt(IDCardType.TAKE_TYPE,takeType)
            .navigation(activity)
    }

    /**
     * 去往实名认证页面
     */
    fun goAuthenticationActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.AuthenticationAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往实名认证页面（简版）
     */
    fun goRealNameActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.RealNameAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往加入公会页面
     */
    fun goJoinGuildActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.JoinGuildAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往新建简历页面
     */
    fun goNewResumeActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.NewResumeAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往人才基本信息页面
     */
    fun goNewTalentBasicActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.TalentBasicAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往聊天页面
     */
    fun goChatActivity(activity: Activity,userAccount: String?,msg: String?) {
        ARouter.getInstance().build(ARouterPath.ChatAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.NIM_ACCOUNT_KEY,userAccount)
            .withString(Constant.INTENT_DATA_KEY,msg)
            .navigation(activity)
    }

    /**
     * 去往聊天页面
     */
    fun goChatActivity(activity: Activity,userAccount: String?) {
        goChatActivity(activity,userAccount,null)
    }

    /**
     * 去往发送位置页面
     */
    fun goSendLocationActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.SendLocationAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往查看位置页面
     */
    fun goViewLocationActivity(activity: Activity,locationAttachment: LocationAttachment?) {
        ARouter.getInstance().build(ARouterPath.ViewLocationAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,locationAttachment)
            .navigation(activity)
    }

    /**
     * 去往人才详情页面
     */
    fun goTalentDetailActivity(activity: Activity,releaseId: String?,action: Int) {
        ARouter.getInstance().build(ARouterPath.TalentDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.RELEASE_ID_KEY,releaseId)
            .withInt(Constant.INTENT_ACTION_KEY,action)
            .navigation(activity)
    }

    /**
     * 去往人才简历详情页面
     */
    fun goTalentResumeDetailActivity(activity: Activity,resumeId: String?) {
        ARouter.getInstance().build(ARouterPath.TalentResumeDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.RESUME_ID_KEY,resumeId)
            .navigation(activity)
    }

    /**
     * 去往雇用详情页面
     */
    fun goJobDetailActivity(activity: Activity,releaseId: String?,
                            talentReleaseId: String?,resumeId: String?, action: Int) {
        ARouter.getInstance().build(ARouterPath.JobDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.RELEASE_ID_KEY,releaseId)
            .withString(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            .withString(Constant.RESUME_ID_KEY,resumeId)
            .withInt(Constant.INTENT_ACTION_KEY,action)
            .navigation(activity)
    }

    /**
     * 去往雇用方详情页面
     */
    fun goHirerDetailActivity(activity: Activity,id: String?) {
        ARouter.getInstance().build(ARouterPath.HirerDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,id)
            .navigation(activity)
    }

    /**
     * 去往充值页面
     */
    fun goRechargeActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.RechargeAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往身份认证页面
     */
    fun goVerifyIdentidyActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.VerifyIdentidyAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往交易密码设置页面
     */
    fun goSetTransactionPasswordActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.SetTransactionPasswordAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往雇佣新增发布页面
     */
    fun goHireNewReleaseActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.HireNewReleaseAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .navigation(activity)
    }

    /**
     * 去往接活发布新增发布页面
     */
    fun goTalentNewReleaseActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.TalentNewReleaseAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .navigation(activity)
    }

    /**
     * 去往邀请人才页面
     */
    fun goInviteTalentActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.InviteTalentAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .navigation(activity)
    }

    /**
     * 去往公会详情页面
     */
    fun goGuildDetailActivity(activity: Activity,guildId: String?,memberType: Int) {
        ARouter.getInstance().build(ARouterPath.GuildDetailAct)
                .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
                .withString(Constant.INTENT_DATA_KEY,guildId)
                .withInt(Constant.MEMBER_TYPE_KEY,memberType)
                .navigation(activity)
    }

    /**
     * 去往雇主详情页面
     */
    fun goEmployerDetailActivity(activity: Activity,data: TaskDetailData?) {
        ARouter.getInstance().build(ARouterPath.EmployerDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.TASK_DETAIL_DATA_KEY,data)
            .navigation(activity)
    }
    fun goEmployerDetailActivity(activity: Activity,data: TalentOrderReleaseInfo?) {
        ARouter.getInstance().build(ARouterPath.EmployerDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.RELEASE_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往我的工作页面
     */
    fun goMyWorkActivity(activity: Activity,pagerPosition: Int) {
        ARouter.getInstance().build(ARouterPath.MyWorkAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withInt(Constant.INTENT_DATA_KEY,pagerPosition)
            .navigation(activity)
    }

    /**
     * 去往我的雇用页面
     */
    fun goMyEmploymentActivity(activity: Activity,pagerPosition: Int) {
        ARouter.getInstance().build(ARouterPath.MyEmploymentAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withInt(Constant.INTENT_DATA_KEY,pagerPosition)
            .navigation(activity)
    }

    /**
     * 去往人才工单详情页面
     */
    fun goTalentOrderDetailActivity(activity: Activity,jobOrderId: String?) {
        ARouter.getInstance().build(ARouterPath.TalentOrderDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,jobOrderId)
            .navigation(activity)
    }

    /**
     * 去往雇主争议处理详情详情页面
     */
    fun goEmployerHandlingDetailActivity(activity: Activity,complaintNo: String?) {
        ARouter.getInstance().build(ARouterPath.EmployerHandlingDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,complaintNo)
            .navigation(activity)
    }

    /**
     * 去往人才争议处理详情详情页面
     */
    fun goTalentHandlingDetailActivity(activity: Activity,complaintNo: String?) {
        ARouter.getInstance().build(ARouterPath.TalentHandlingDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,complaintNo)
            .navigation(activity)
    }

    /**
     * 去往明细详情详情页面
     */
    fun goBalanceFlowDetailActivity(activity: Activity,data: BalanceFlowInfo?) {
        ARouter.getInstance().build(ARouterPath.BalanceFlowDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往会长工会页面
     */
    fun goPresidentGuildActivity(activity: Activity,data: MyGuildData?) {
        ARouter.getInstance().build(ARouterPath.PresidentGuildAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往会长工会页面
     */
    fun goMyGuildActivity(activity: Activity,data: MyGuildData?) {
        ARouter.getInstance().build(ARouterPath.MyGuildAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往卡券包页面
     */
    fun goCouponActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.CouponAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往新增雇主页面
     */
    fun goNewEmployerActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.NewEmployerAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往雇主基本信息页面
     */
    fun goEmployerBasicActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.EmployerBasicAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往任务新增发布页面
     */
    fun goTaskNewReleaseActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.TaskNewReleaseAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往任务编辑发布页面
     */
    fun goTaskUpdateReleaseAct(activity: Activity,data: EmployerReleaseInfo?, status: Int) {
        ARouter.getInstance().build(ARouterPath.TaskUpdateReleaseAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .withSerializable(Constant.STATUS_KEY,status)
            .navigation(activity)
    }

    /**
     * 去往任务搜索页面
     */
    fun goTaskSearchActivity(activity: Activity) {
        ARouter.getInstance().build(ARouterPath.TaskSearchAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .navigation(activity)
    }

    /**
     * 去往我的雇用
     */
    fun goHireReleaseActivity(activity: Activity,pagerPosition: Int) {
        ARouter.getInstance().build(ARouterPath.HireReleaseAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withInt(Constant.INTENT_DATA_KEY,pagerPosition)
            .navigation(activity)
    }

    /**
     * 去往任务详情
     */
    fun goTaskDetailActivity(activity: Activity,releaseId: String?,
                             talentReleaseId: String?,resumeId: String?, action: Int) {
        ARouter.getInstance().build(ARouterPath.TaskDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.RELEASE_ID_KEY,releaseId)
            .withString(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            .withString(Constant.RESUME_ID_KEY,resumeId)
            .withInt(Constant.INTENT_ACTION_KEY,action)
            .navigation(activity)
    }

    /**
     * 去往我的雇用
     */
    fun goEmployerAllEvaluationActivity(activity: Activity,employerId: String?,label: Int) {
        ARouter.getInstance().build(ARouterPath.EmployerAllEvaluationAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,employerId)
            .withInt(Constant.LABEL_KEY,label)
            .navigation(activity)
    }

    /**
     * 去往任务提交
     */
    fun goSubmitTaskActivity(activity: Activity,data: TaskSettlementDetailData?) {
        ARouter.getInstance().build(ARouterPath.SubmitTaskAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往任务提交详情
     */
    fun goTaskSubmitDetailActivity(activity: Activity,data: TaskSubmitDetailData?) {
        ARouter.getInstance().build(ARouterPath.TaskSubmitDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

    /**
     * 去往任务工单详情
     */
    fun goTaskOrderDetailActivity(activity: Activity,jobOrderId: String?) {
        ARouter.getInstance().build(ARouterPath.TaskOrderDetailAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withString(Constant.INTENT_DATA_KEY,jobOrderId)
            .navigation(activity)
    }

    /**
     * 去往任务结算详情
     */
    fun goTaskSettlementSalaryActivity(activity: Activity,data: TaskSettlementConfirmDetailParm?) {
        ARouter.getInstance().build(ARouterPath.TaskSettlementSalaryAct)
            .withTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
            .withSerializable(Constant.INTENT_DATA_KEY,data)
            .navigation(activity)
    }

}