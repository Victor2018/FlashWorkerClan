package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.coremodel.data.req.AccountInfoReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.act_wallet_header.*
import kotlinx.android.synthetic.main.act_wallet_header.mTvAccountBalance
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.activity_wallet.appbar
import kotlinx.android.synthetic.main.activity_wallet.mSrlRefresh

class WalletActivity : BaseActivity(), View.OnClickListener,AppBarLayout.OnOffsetChangedListener,
    SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, WalletActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mAccountInfoReq: AccountInfoReq? = null

    override fun getLayoutResource() = R.layout.activity_wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        appbar.addOnOffsetChangedListener(this)

        mIvBack.setOnClickListener(this)
        mViewFrozenFlow.setOnClickListener(this)
        mTvBalanceDetail.setOnClickListener(this)
        mTvRecharge.setOnClickListener(this)
        mTvWithdraw.setOnClickListener(this)

    }

    fun initData () {
        sendAccountInfoRequest()
    }

    fun subscribeUi () {
        accountVM.accountInfoData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showAccountInfo(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendAccountInfoRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showAccountInfo (data: AccountInfoReq?) {
        mAccountInfoReq = data
        mTvAccountBalance.setNumberString(AmountUtil.addCommaDots(data?.data?.totalBalance).toString())
        mTvAvailableBalance.text = AmountUtil.addCommaDots(data?.data?.availableBalance)
        mTvCreditFreeze.text = AmountUtil.addCommaDots(data?.data?.frozenAmount)
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                SetTransactionPasswordActivity.intentStart(this@WalletActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时不能体现哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                RealNameActivity.intentStart(this@WalletActivity)
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
            R.id.mViewFrozenFlow -> {
                FrozenFlowActivity.intentStart(this)
            }
            R.id.mTvBalanceDetail -> {
                BalanceFlowActivity.intentStart(this)
            }
            R.id.mTvRecharge -> {
                RechargeActivity.intentStart(this)
            }
            R.id.mTvWithdraw -> {
                var hasTradePassword = mAccountInfoReq?.data?.hasTradePassword ?: false
                if (!hasTradePassword) {
                    showSetTransactionPwdDlg()
                    return
                }
                var userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 0) {
                    showAuthTipDlg()
                    return
                }

                WithdrawActivity.intentStart(this,mAccountInfoReq?.data?.availableBalance)
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

    override fun onRefresh() {
        sendAccountInfoRequest()
    }
}