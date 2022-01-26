package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.PrepaidConfirmDetailData
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_prepaid_salary_success.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PrepaidSalarySuccessActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class PrepaidSalarySuccessActivity: BaseActivity(),View.OnClickListener {

    var passwordErrorCount: Int = 0//密码输入错误次数

    var mPrepaidConfirmDetailData: PrepaidConfirmDetailData? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: PrepaidConfirmDetailData?) {
            var intent = Intent(activity, PrepaidSalarySuccessActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_prepaid_salary_success

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvConfim.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mPrepaidConfirmDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as PrepaidConfirmDetailData?

        mTvPrepaidAmount.text = AmountUtil.addCommaDots(mPrepaidConfirmDetailData?.totalPrepaidAmount)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfim -> {
                onBackPressed()
            }
        }
    }

}