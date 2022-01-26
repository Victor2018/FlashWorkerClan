package com.flash.worker.module.login.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.module.login.R
import kotlinx.android.synthetic.main.activity_account_frozen.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountFrozenActivity
 * Author: Victor
 * Date: 2020/12/10 19:37
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.AccountFrozenAct)
class AccountFrozenActivity: BaseActivity(),View.OnClickListener {

    companion object {
        fun intentStart(activity: AppCompatActivity,msg: String?) {
            var intent = Intent(activity, AccountFrozenActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,msg)
            activity.startActivity(intent)
        }
    }

    var msg: String? = null

    override fun getLayoutResource() = R.layout.activity_account_frozen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        msg = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        mTvTip.text = "很抱歉！您的账号违反了闪工族平台规则，\n已被冻结,解冻时间为$msg"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}