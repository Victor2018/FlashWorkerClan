package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.activity_setting

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
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvAccountSecurity.setOnClickListener(this)
        mClVerified.setOnClickListener(this)
        mClCancelAccount.setOnClickListener(this)
        mClClearCache.setOnClickListener(this)
    }

    fun initData () {
        try {
            var text: String? = CacheCleanUtils.getTotalCacheSize(this)
            mTvClearCache.setText(text)
        } catch (e: Exception) {
            e.printStackTrace()
            mTvClearCache.text = "0KB"
        }

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            TextViewBoundsUtil.setTvDrawableLeft(this,mTvVerified,R.mipmap.ic_mine_verified)
            mTvVerified.text = "已认证"
        } else {
            TextViewBoundsUtil.setTvDrawableRight(this,mTvVerified,R.mipmap.ic_right)
            mTvVerified.text = "未认证"
        }
    }

    fun subscribeUi () {
        accountVM.accountInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var hasTradePassword = it.value?.data?.hasTradePassword ?: false
                    if (!hasTradePassword) {
                        showSetTransactionPwdDlg()
                        return@Observer
                    }
                    AccountSecurityActivity.intentStart(this)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }


    fun subscribeEvent() {
        LiveDataBus.with(MineActions.BACK_SETTING_ACT)
            .observe(this, Observer {
                finish()
            })

    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码，暂时不能修改账户信息哦~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                SetTransactionPasswordActivity.intentStart(this@SettingActivity)
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
            R.id.mTvAccountSecurity -> {
                sendAccountInfoRequest()
            }
            R.id.mClVerified -> {
                val userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 1) {
                    ToastUtils.show("已通过实名认证")
                    return
                }
                RealNameActivity.intentStart(this)
            }
            R.id.mClClearCache -> {
                App.get().setCityData(null)
                CacheCleanUtils.clearAllCache(this)
                mTvClearCache.text = "0KB"
                ToastUtils.show("缓存已清除")

                UMengEventModule.report(this, MineEvent.clear_cache)
            }
            R.id.mClCancelAccount -> {
                CancelAccountActivity.intentStart(this)
            }
        }
    }
}