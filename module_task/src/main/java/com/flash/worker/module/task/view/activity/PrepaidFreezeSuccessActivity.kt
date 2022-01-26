package com.flash.worker.module.task.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import kotlinx.android.synthetic.main.activity_prepaid_freeze_success.*

class PrepaidFreezeSuccessActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,totalPrepaidAmount: Double?) {
            var intent = Intent(activity, PrepaidFreezeSuccessActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,totalPrepaidAmount)
            activity.startActivity(intent)
        }
    }

    var totalPrepaidAmount: Double = 0.0

    override fun getLayoutResource() = R.layout.activity_prepaid_freeze_success

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvHome.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        totalPrepaidAmount = intent?.getDoubleExtra(Constant.INTENT_DATA_KEY,0.0) ?: 0.0
        mTvTotalPrepaidAmount.text = AmountUtil.addCommaDots(totalPrepaidAmount)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                LiveDataBus.send(JobActions.EXIT_HIRE_RELEASE)
                onBackPressed()
            }
            R.id.mTvHome -> {
                NavigationUtils.goHireReleaseActivity(this,3)
                onBackPressed()
            }
        }
    }
}