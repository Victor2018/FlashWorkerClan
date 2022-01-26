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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.parm.ModifyTradePasswordParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.CryptoUtils
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_modify_transaction_pwd.*
import kotlinx.android.synthetic.main.activity_reset_transaction_pwd.mEtTransactionConfirmPwd
import kotlinx.android.synthetic.main.activity_reset_transaction_pwd.mEtTransactionPwd
import kotlinx.android.synthetic.main.activity_reset_transaction_pwd.mIvBack
import kotlinx.android.synthetic.main.activity_reset_transaction_pwd.mTvConfirm

class ModifyTransactionPasswordActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ModifyTransactionPasswordActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.activity_modify_transaction_pwd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }


    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun subscribeUi () {
        accountVM.modifyTradePwdData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("交易密码修改成功")
                    UMengEventModule.report(this, MineEvent.edit_transaction_password)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendModifyTradePwdRequest () {
        var oldPwd = mEtOldPwd.text.toString()
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtils.show("请输入原密码")
            return
        }
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

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ModifyTradePasswordParm()

        body.oldTradePassword = CryptoUtils.MD5(oldPwd)
        body.newTradePassword = CryptoUtils.MD5(pwdConfirm)

        accountVM.modifyTradePwd(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                sendModifyTradePwdRequest()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }


}