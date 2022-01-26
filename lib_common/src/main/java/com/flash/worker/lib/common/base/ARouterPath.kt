package com.flash.worker.lib.common.base

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ARouterPath
 * Author: Victor
 * Date: 2020/7/3 下午 06:41
 * Description: 
 * -----------------------------------------------------------------
 */
object ARouterPath {
    private const val SERVICE = "/service"

    private const val LOGIN = "/login"
    private const val MAIN = "/main"
    private const val CAMERA = "/camera"

    private const val HIRE = "/hire"
    private const val JOB = "/job"
    private const val BUSINESS = "/business"
    private const val MESSAGE = "/message"
    private const val MINE = "/mine"
    private const val TASK = "/task"

    /**登录服务 */
    const val loginService = "$SERVICE/common/LoginService"

    const val CodeLoginAct = "$LOGIN/module/CodeLoginActivity"
    const val BindPhoneAct = "$LOGIN/module/BindPhoneActivity"
    const val AccountFrozenAct = "$LOGIN/module/AccountFrozenActivity"
    const val AccountCancelledAct = "$LOGIN/module/AccountCancelledActivity"

    /**主 Activity */
    const val MainAct = "$MAIN/app/MainActivity"

    /**相机 Activity */
    const val CameraAct = "$CAMERA/app/CameraActivity"

    /**首页 Activity */
    const val HireAct = "$HIRE/module/HireActivity"
    /**雇用 Fragment */
    const val HireFgt = "$HIRE/module/HireFragment"

    /**人才详情 Activity */
    const val TalentDetailAct = "$HIRE/module/TalentDetailActivity"

    /**人才全部评价 Activity */
    const val TalentAllEvaluationAct = "$HIRE/module/TalentAllEvaluationActivity"

    /**人才简历详情 Activity */
    const val TalentResumeDetailAct = "$HIRE/module/TalentResumeDetailActivity"

    /**邀请人才 Activity */
    const val InviteTalentAct = "$HIRE/module/InviteTalentActivity"

    /**雇用详情 Activity */
    const val JobDetailAct = "$JOB/module/JobDetailActivity"

    /**找活 Fragment */
    const val JobFgt = "$JOB/module/JobFragment"

    /**找活 Activity */
    const val JobAct = "$JOB/module/JobActivity"

    /**雇主详情 Activity */
    const val HirerDetailAct = "$JOB/module/HirerDetailActivity"

    /**雇主全部评价 Activity */
    const val EmployerAllEvaluationAct = "$JOB/module/EmployerAllEvaluationActivity"

    /**雇主详情 Activity */
    const val EmployerDetailAct = "$JOB/module/EmployerDetailActivity"

    /**操作台 Fragment */
    const val BusinessFgt = "$BUSINESS/module/BusinessFragment"

    /**操作台 Activity */
    const val BusinessAct = "$BUSINESS/module/BusinessActivity"

    /**实名认证 Activity */
    const val AuthenticationAct = "$BUSINESS/module/AuthenticationActivity"

    /**新建简历 Activity */
    const val NewResumeAct = "$BUSINESS/module/NewResumeActivity"

    /**人才基本信息 Activity */
    const val TalentBasicAct = "$BUSINESS/module/TalentBasicActivity"

    /**雇佣新增发布 Activity */
    const val HireNewReleaseAct = "$BUSINESS/module/HireNewReleaseActivity"

    /**接活新增发布 Activity */
    const val TalentNewReleaseAct = "$BUSINESS/module/TalentNewReleaseActivity"

    /**我的工作 Activity */
    const val MyWorkAct = "$BUSINESS/module/MyWorkActivity"

    /**我的雇用 Activity */
    const val MyEmploymentAct = "$BUSINESS/module/MyEmploymentActivity"

    /**雇主争议处理详情 Activity */
    const val EmployerHandlingDetailAct = "$BUSINESS/module/EmployerHandlingDetailActivity"

    /**人才争议处理详情 Activity */
    const val TalentHandlingDetailAct = "$BUSINESS/module/TalentHandlingDetailActivity"

    /**人才工单详情 Activity */
    const val TalentOrderDetailAct = "$BUSINESS/module/TalentOrderDetailActivity"

    /**新增雇主 Activity */
    const val NewEmployerAct = "$BUSINESS/module/NewEmployerActivity"

    /**雇主基本信息 Activity */
    const val EmployerBasicAct = "$BUSINESS/module/EmployerBasicActivity"

    /**我的雇用 Activity */
    const val HireReleaseAct = "$BUSINESS/module/HireReleaseActivity"

    /**消息 Fragment */
    const val MessageFgt = "$MESSAGE/module/MessageFragment"
    /**消息 Activity */
    const val MessageAct = "$MESSAGE/module/MessageActivity"

    /**聊天 Activity */
    const val ChatAct = "$MESSAGE/module/ChatActivity"

    /**发送位置 Activity */
    const val SendLocationAct = "$MESSAGE/module/SendLocationActivity"

    /**查看位置 Activity */
    const val ViewLocationAct = "$MESSAGE/module/ViewLocationActivity"


    /**我的 Fragment */
    const val MineFgt = "$MINE/module/MineFragment"

    /**我的 Activity */
    const val MineAct = "$MINE/module/MineAct"

    /**充值 Activity */
    const val RechargeAct = "$MINE/module/RechargeActivity"

    /**验证身份 Activity */
    const val VerifyIdentidyAct = "$MINE/module/VerifyIdentidyActivity"

    /**设置交易密码 Activity */
    const val SetTransactionPasswordAct = "$MINE/module/SetTransactionPasswordActivity"

    /**公会详情 Activity */
    const val GuildDetailAct = "$MINE/module/GuildDetailActivity"

    /**实名认证 Activity */
    const val RealNameAct = "$MINE/module/RealNameActivity"
    /**加入公会 Activity */
    const val JoinGuildAct = "$MINE/module/JoinGuildActivity"
    /**明细详情 Activity */
    const val BalanceFlowDetailAct = "$MINE/module/BalanceFlowDetailActivity"
    /**会长工会 Activity */
    const val PresidentGuildAct = "$MINE/module/PresidentGuildActivity"
    /**成员工会 Activity */
    const val MyGuildAct = "$MINE/module/MyGuildActivity"
    /**卡券包 Activity */
    const val CouponAct = "$MINE/module/CouponActivity"


    /**任务新增发布 Activity */
    const val TaskNewReleaseAct = "$TASK/module/TaskNewReleaseActivity"

    /**任务修改发布 Activity */
    const val TaskUpdateReleaseAct = "$TASK/module/TaskUpdateReleaseActivity"

    /**任务搜索 Activity */
    const val TaskSearchAct = "$TASK/module/TaskSearchActivity"

    /**任务详情 Activity */
    const val TaskDetailAct = "$TASK/module/TaskDetailActivity"

    /**任务提交 Activity */
    const val SubmitTaskAct = "$TASK/module/SubmitTaskActivity"

    /**任务提交 Activity */
    const val TaskSubmitDetailAct = "$TASK/module/TaskSubmitDetailActivity"

    /**任务工单详情 Activity */
    const val TaskOrderDetailAct = "$TASK/module/TaskOrderDetailActivity"

    /**任务结算详情 Activity */
    const val TaskSettlementSalaryAct = "$TASK/module/TaskSettlementSalaryActivity"

}