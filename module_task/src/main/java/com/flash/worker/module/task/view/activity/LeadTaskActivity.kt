package com.flash.worker.module.task.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.ReceiveTaskDetailReq
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.TaskActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import com.flash.worker.module.task.interfaces.OnTaskCountInputListener
import com.flash.worker.module.task.view.dialog.TaskCountInputDialog
import kotlinx.android.synthetic.main.activity_lead_task.*

class LeadTaskActivity : BaseActivity(),View.OnClickListener, OnTradePasswordListener,
    OnTaskCountInputListener {

    companion object {
        fun intentStart (
            activity: AppCompatActivity,
            body: ReceiveTaskDetailParm?,resumeId: String?,talentReleaseId: String?) {
            var intent = Intent(activity, LeadTaskActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,body)
            intent.putExtra(Constant.RESUME_ID_KEY,resumeId)
            intent.putExtra(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)

            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    var mReceiveTaskDetailParm: ReceiveTaskDetailParm? = null
    var resumeId: String? = null//人才简历ID
    var talentReleaseId: String? = null//人才发布ID

    var mReceiveTaskDetailData: ReceiveTaskDetailData? = null

    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.activity_lead_task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirmLead.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mReceiveTaskDetailParm = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as ReceiveTaskDetailParm?

        resumeId = intent?.getStringExtra(Constant.RESUME_ID_KEY)
        talentReleaseId = intent?.getStringExtra(Constant.TALENT_RELEASE_ID_KEY)

        sendReceiveTaskDetailRequest()
    }

    fun subscribeUi() {
        accountVM.accountInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var hasTradePassword = it.value?.data?.hasTradePassword ?: false
                    if (!hasTradePassword) {
                        showSetTransactionPwdDlg()
                        return@Observer
                    }
                    getTradePasswordDialog().show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.receiveTaskDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showReceiveTaskDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.receiveTaskData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    LeadTaskSuccessActivity.intentStart(this)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("151135",it.code)) {
                        if (passwordErrorCount >= 4) {
                            LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                            showErrorPasswordTipDlg()
                            passwordErrorCount = 0
                        }
                        LiveDataBus.send(JobActions.TRADE_PASSWORD_ERROR)
                        passwordErrorCount++
                        return@Observer
                    }
                    if (TextUtils.equals("156010",it.code)) {
                        LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                        sendReceiveTaskDetailRequest()
                        return@Observer
                    }
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendReceiveTaskDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchReceiveTaskDetail(token,mReceiveTaskDetailParm)
    }

    fun sendReceiveTaskRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ReceiveTaskParm()
        body.employerReleaseId = mReceiveTaskDetailParm?.employerReleaseId
        body.resumeId = resumeId
        body.talentReleaseId = talentReleaseId
        if (TextUtils.isEmpty(talentReleaseId)) {
            body.source = 1
        } else {
            body.source = 2
        }

        body.taskReceiveQty = mReceiveTaskDetailParm?.taskReceiveQty ?: 0
        body.tradePassword = tradePassword

        employmentVM.receiveTask(token,body)
    }


    fun showReceiveTaskDetailData (data: ReceiveTaskDetailReq) {

        var taskReceiveQty = data.data?.taskReceiveQty ?: 0
        var taskRemainQty = data.data?.taskRemainQty ?: 0
        if (taskRemainQty == 0) {
            showTaskReceiveFinishTipDlg()
            return
        }

        if (taskRemainQty < taskReceiveQty) {
            showTaskCountInputDlg(taskRemainQty)
            return
        }

        mReceiveTaskDetailData = data.data

        mTvTaskName.text = data.data?.title
        mTvPrice.text = "${AmountUtil.addCommaDots(data.data?.price)}元"
        mTvCount.text = "${data.data?.taskReceiveQty}件"
        mTvSubtotal.text = "${AmountUtil.addCommaDots(data.data?.settlementAmount)}元"
        mTvAccountBalance.text = AmountUtil.addCommaDots(data.data?.availableBalance)
        tv_credit_freeze_amount.text = "信用冻结(${data.data?.creditFrozenRate}%):"
        mTvCreditFreezeAmount.text = "${AmountUtil.addCommaDots(data.data?.creditFrozenAmount)}元"

        var availableBalance = data.data?.availableBalance ?: 0.0
        var creditFrozenAmount = data.data?.creditFrozenAmount ?: 0.0
        if (availableBalance < creditFrozenAmount   ) {
            mTvConfirmLead.text = "余额充值"
        } else {
            mTvConfirmLead.text = "确认领取"
        }

    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog =
            TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@LeadTaskActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已连续输错密码5次，\n" +
                "若连续输错密码10次 ，\n" +
                "您的帐户将被锁定2小时。"
        commonTipDialog.mCancelText = "忘记密码"
        commonTipDialog.mOkText = "重试"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                getTradePasswordDialog().show()
            }

            override fun OnDialogCancelClick() {
                var userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 0) {
                    showAuthTipDlg()
                    return
                }
                NavigationUtils.goVerifyIdentidyActivity(this@LeadTaskActivity)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时不能修改提现密码哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@LeadTaskActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    /**
     * 显示任务被领完弹窗
     */
    fun showTaskReceiveFinishTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "很抱歉，该任务已被领完，请返回领取其他任务吧！"
        commonTipDialog.mOkText = "回任务列表"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                LiveDataBus.send(TaskActions.EXIT_TASK_DETAIL)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    fun showTaskCountInputDlg(maxCount: Int) {
        var taskCountInputDialog = TaskCountInputDialog(this)
        taskCountInputDialog.maxCount = maxCount
        taskCountInputDialog.mTitleTxt = "当前任务剩余${maxCount}件，\n是否继续领取"
        taskCountInputDialog.mOnTaskCountInputListener = this
        taskCountInputDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirmLead -> {
                var availableBalance = mReceiveTaskDetailData?.availableBalance ?: 0.0
                var creditFrozenAmount = mReceiveTaskDetailData?.creditFrozenAmount ?: 0.0
                if (availableBalance < creditFrozenAmount) {
                    NavigationUtils.goRechargeActivity(this)
                    return
                }
                sendAccountInfoRequest()
            }
        }
    }

    override fun OnTaskCountInput(count: Int) {
        mReceiveTaskDetailParm?.taskReceiveQty = count
        sendReceiveTaskDetailRequest()
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendReceiveTaskRequest(tradePassword)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}