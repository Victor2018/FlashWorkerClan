package com.flash.worker.module.task.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import kotlinx.android.synthetic.main.activity_lead_task_success.*

class LeadTaskSuccessActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, LeadTaskSuccessActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_lead_task_success

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvHome.setOnClickListener(this)
    }

    fun initData () {
        var spannText = "操作台- 人才- 进行中"
        var text = "您可在 操作台- 人才- 进行中 中查看进程。"
        SpannableUtil.setSpannableColor(
            mTvLeadSucccess, ResUtils.getColorRes(R.color.color_E26853),text,spannText)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvHome -> {
                LiveDataBus.send(JobActions.BACK_HOME)
                onBackPressed()
            }
        }
    }
}