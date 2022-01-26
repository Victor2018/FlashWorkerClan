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
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitEmployInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ReleaseTaskParms
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo
import com.flash.worker.lib.coremodel.data.parm.EmployConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.EmployerEmploymentParm
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.data.req.EmployConfirmDetailReq
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.TaskVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.TaskActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import kotlinx.android.synthetic.main.activity_prepaid_freeze.*
import kotlinx.android.synthetic.main.task_release_content.*

class PrepaidFreezeActivity : BaseActivity(),View.OnClickListener,
    OnTradePasswordListener {

    companion object {
        fun intentStart (activity: AppCompatActivity, body: ReleaseTaskParms?) {
            var intent = Intent(activity, PrepaidFreezeActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,body)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    private val taskVM: TaskVM by viewModels {
        InjectorUtils.provideTaskVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    var mReleaseTaskParms: ReleaseTaskParms? = null

    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.activity_prepaid_freeze

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
        mTvConfirmAdmission.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mReleaseTaskParms = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as ReleaseTaskParms?

        sendTaskPrepaidDetailRequest()
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

        taskVM.releaseTaskData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(TaskActions.RELEASE_TASK_SUCCESS)
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    PrepaidFreezeSuccessActivity.intentStart(this,
                        mReleaseTaskParms?.taskPrepaidDetailData?.totalPrepaidAmount)
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
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message)
                }
            }
        })

        taskVM.updateReleaseTaskData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(TaskActions.RELEASE_TASK_SUCCESS)
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    PrepaidFreezeSuccessActivity.intentStart(this,
                        mReleaseTaskParms?.taskPrepaidDetailData?.totalPrepaidAmount)
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
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message)
                }
            }
        })

        taskVM.taskPrepaidDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mReleaseTaskParms?.taskPrepaidDetailData = it.value.data
                    showTaskPrepaidDetailData()
                }
                is HttpResult.Error -> {
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

    fun sendReleaseTaskRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = mReleaseTaskParms?.body
        body?.tradePassword = tradePassword

        var status = mReleaseTaskParms?.status ?: 0
        if (status == 1) {//编辑中
            taskVM.updateReleaseTask(token,body)
        } else {
            taskVM.releaseTask(token,body)
        }
    }

    fun sendTaskPrepaidDetailRequest() {
        if (!App.get().hasLogin()) return

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        mLoadingDialog?.show()

        var body = TaskPrepaidDetailParm()
        body.title = mReleaseTaskParms?.body?.title
        body.price = AmountUtil.getRoundUpDouble(mReleaseTaskParms?.body?.price?.toDouble(),2)
        body.taskQty = mReleaseTaskParms?.body?.taskQty ?: 0

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        taskVM.fetchTaskPrepaidDetail(token,body)
    }

    fun showTaskPrepaidDetailData () {
        mTvTaskName.text = mReleaseTaskParms?.taskPrepaidDetailData?.title
        mTvPrice.text = "${AmountUtil.addCommaDots(mReleaseTaskParms?.taskPrepaidDetailData?.price)}元"
        mTvCount.text = "${mReleaseTaskParms?.taskPrepaidDetailData?.taskQty}件"
        mTvSubtotal.text = "${AmountUtil.addCommaDots(mReleaseTaskParms?.taskPrepaidDetailData?.settlementAmount)}元"
        mTvServiceAmount.text = "${AmountUtil.addCommaDots(mReleaseTaskParms?.taskPrepaidDetailData?.serviceFeeAmount)}元"
        mTvTotalAmount.text = AmountUtil.addCommaDots(mReleaseTaskParms?.taskPrepaidDetailData?.totalPrepaidAmount)
        mTvAccountBalance.text = AmountUtil.addCommaDots(mReleaseTaskParms?.taskPrepaidDetailData?.availableBalance)

        var availableBalance = mReleaseTaskParms?.taskPrepaidDetailData?.availableBalance ?: 0.0
        var totalPrepaidAmount = mReleaseTaskParms?.taskPrepaidDetailData?.totalPrepaidAmount ?: 0.0
        if (availableBalance < totalPrepaidAmount) {
            mTvConfirmAdmission.text = "余额充值"
        } else {
            mTvConfirmAdmission.text = "确认预付"
        }

    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(this)
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
                NavigationUtils.goSetTransactionPasswordActivity(this@PrepaidFreezeActivity)
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
                NavigationUtils.goVerifyIdentidyActivity(this@PrepaidFreezeActivity)
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
                NavigationUtils.goRealNameActivity(this@PrepaidFreezeActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showPrepaidFreezeTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "\t\t\t\t当任务处于无人领取状态时，\n" +
                "您可随时关闭任务，已预付金额将\n" +
                "即时解冻。"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确认"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAccountInfoRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirmAdmission -> {
                var availableBalance = mReleaseTaskParms?.taskPrepaidDetailData?.availableBalance ?: 0.0
                var totalPrepaidAmount = mReleaseTaskParms?.taskPrepaidDetailData?.totalPrepaidAmount ?: 0.0
                if (availableBalance < totalPrepaidAmount) {
                    NavigationUtils.goRechargeActivity(this)
                    return
                }
                showPrepaidFreezeTipDlg()
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendReleaseTaskRequest(tradePassword)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}