package com.flash.worker.module.task.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementConfirmDetailData
import com.flash.worker.module.task.R
import kotlinx.android.synthetic.main.activity_task_settlement_salary_success.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementSalarySuccessActivity
 * Author: Victor
 * Date: 2021/12/13 15:31
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementSalarySuccessActivity: BaseActivity(), View.OnClickListener {

    var mTaskSettlementBillDetailData: TaskSettlementConfirmDetailData? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: TaskSettlementConfirmDetailData?) {
            var intent = Intent(activity, TaskSettlementSalarySuccessActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_task_settlement_salary_success

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
        mTaskSettlementBillDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as TaskSettlementConfirmDetailData?

        mTvSettlementAmount.text = AmountUtil.addCommaDots(mTaskSettlementBillDetailData?.totalSettledAmount)
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