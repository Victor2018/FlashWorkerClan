package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_account_security.*

class AccountSecurityActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, AccountSecurityActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_account_security

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        subscribeEvent()

        mIvBack.setOnClickListener(this)
        mTvModifyTradePwd.setOnClickListener(this)
        mTvResetTradePwd.setOnClickListener(this)
        mTvBindAccount.setOnClickListener(this)
    }

    fun subscribeEvent() {
        LiveDataBus.with(MineActions.BACK_ACCOUNT_SECURITY_ACT)
            .observe(this, Observer {
                LiveDataBus.send(MineActions.BACK_SETTING_ACT)
                finish()
            })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvModifyTradePwd -> {
                ModifyTransactionPasswordActivity.intentStart(this)
            }
            R.id.mTvResetTradePwd -> {
                VerifyIdentidyActivity.intentStart(this)
            }
            R.id.mTvBindAccount -> {
                WithdrawAccountBindingActivity.intentStart(this)
            }
        }
    }
}