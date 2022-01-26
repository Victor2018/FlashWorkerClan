package com.flash.worker.module.task.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementConfirmDetailData
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import com.flash.worker.module.task.view.adapter.TaskSettlementUserAdapter
import kotlinx.android.synthetic.main.activity_task_settlement_salary.*

@Route(path = ARouterPath.TaskSettlementSalaryAct)
class TaskSettlementSalaryActivity: BaseActivity(), View.OnClickListener, OnTradePasswordListener,
    AdapterView.OnItemClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    var mTaskSettlementBillDetailParm: TaskSettlementConfirmDetailParm? = null
    var mTaskSettlementBillDetailData: TaskSettlementConfirmDetailData? = null

    var mTaskSettlementUserAdapter: TaskSettlementUserAdapter? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: TaskSettlementConfirmDetailParm?) {
            var intent = Intent(activity, TaskSettlementSalaryActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }
    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_task_settlement_salary

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
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

        mTaskSettlementUserAdapter = TaskSettlementUserAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTaskSettlementUserAdapter,mRvSalaryTalent)

        mRvSalaryTalent.adapter = animatorAdapter

        mIvBack.setOnClickListener(this)
        mTvConfimSettle.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mTaskSettlementBillDetailParm = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as TaskSettlementConfirmDetailParm

        sendSettlementConfirmDetailRequest()
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
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.taskSettlementConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showTaskSettlementConfirmDetailData(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.taskSettlementData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    TaskSettlementSalarySuccessActivity.intentStart(this,mTaskSettlementBillDetailData)

                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
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
                    ToastUtils.show(it.message.toString())
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

    fun sendSettlementConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchTaskSettlementConfirmDetail(token,mTaskSettlementBillDetailParm)
    }

    fun sendTaskSettlementRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TaskSettlementParm()
        body.settlementOrderIds = mTaskSettlementBillDetailParm?.settlementOrderIds
        body.employerReleaseId = mTaskSettlementBillDetailParm?.employerReleaseId
        body.tradePassword = tradePassword

        employmentVM.taskSettlement(token,body)
    }

    fun showTaskSettlementConfirmDetailData (data: TaskSettlementConfirmDetailData?) {
        mTaskSettlementBillDetailData = data

        mTvSettledCount.text = data?.count.toString()
        tv_service_fee_rate.text = "平台服务费(${data?.serviceFeeRate}%)："

        mTvCreditFreezeAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)
        mTvTotalSettledAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)

        mTaskSettlementUserAdapter?.showData(data?.settlementUsers)

    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@TaskSettlementSalaryActivity)
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
                NavigationUtils.goVerifyIdentidyActivity(this@TaskSettlementSalaryActivity)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@TaskSettlementSalaryActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfimSettle -> {
                sendAccountInfoRequest()
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendTaskSettlementRequest(tradePassword)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }


}