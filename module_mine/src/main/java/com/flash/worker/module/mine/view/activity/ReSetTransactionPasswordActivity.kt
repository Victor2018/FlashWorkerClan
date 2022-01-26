package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.parm.ResetTradePwdParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.CryptoUtils
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_reset_transaction_pwd.*

class ReSetTransactionPasswordActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,realName: String?,idCard: String?,code: String?) {
            var intent = Intent(activity, ReSetTransactionPasswordActivity::class.java)
            intent.putExtra(Constant.REAL_NAME_KEY,realName)
            intent.putExtra(Constant.ID_CARD_KEY,idCard)
            intent.putExtra(Constant.CODE_KEY,code)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var realName: String? = null
    var idCard: String? = null
    var code: String? = null

    override fun getLayoutResource() = R.layout.activity_reset_transaction_pwd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
        initData(intent)
    }


    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        realName = intent?.getStringExtra(Constant.REAL_NAME_KEY)
        idCard = intent?.getStringExtra(Constant.ID_CARD_KEY)
        code = intent?.getStringExtra(Constant.CODE_KEY)
    }

    fun subscribeUi () {
        accountVM.resetTradePwdData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ReSetTransactionPasswordSuccessActivity.intentStart(this)
                    UMengEventModule.report(this, MineEvent.reset_transaction_password)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendResetTradePwdRequest (tradePassword: String) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ResetTradePwdParm()
        body.newTradePassword = CryptoUtils.MD5(tradePassword)
        body.realName = realName
        body.idcard = idCard
        body.code = code

        accountVM.resetTradePwd(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                var pwd = mEtTransactionPwd.text.toString()
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.show("请输入交易密码")
                    return
                }
                var pwdConfirm = mEtTransactionConfirmPwd.text.toString()
                if (TextUtils.isEmpty(pwdConfirm)) {
                    ToastUtils.show("请再次输入交易密码")
                    return
                }

                if (pwd.length < 6) {
                    ToastUtils.show("交易密码为6位数字")
                    return
                }

                if (!TextUtils.equals(pwd,pwdConfirm)) {
                    ToastUtils.show("两次输入交易密码不一致")
                    return
                }

                sendResetTradePwdRequest(pwd)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }


}