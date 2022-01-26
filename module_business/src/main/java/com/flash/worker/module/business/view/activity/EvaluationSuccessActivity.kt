package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_evaluation_success.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EvaluationSuccessActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class EvaluationSuccessActivity: BaseActivity(),View.OnClickListener {

    var isTalentEvaluation: Boolean = false

    companion object {
        fun  intentStart (activity: AppCompatActivity,isTalentEvaluation: Boolean) {
            var intent = Intent(activity, EvaluationSuccessActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,isTalentEvaluation)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_evaluation_success

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvViewEvaluation.setOnClickListener(this)
    }

    fun initData(intent: Intent?) {
        isTalentEvaluation = intent?.getBooleanExtra(Constant.INTENT_DATA_KEY,false) ?: false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvViewEvaluation -> {
                if (isTalentEvaluation) {
                    TalentEvaluationCenterActivity.intentStart(this)
                } else {
                    EmployerEvaluationCenterActivity.intentStart(this)
                }
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}