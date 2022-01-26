package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_employ_success.*

class EmploySuccessActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EmploySuccessActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employ_success

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
        var spannText = "操作台- 雇主- 全部雇用"
        var text = "您可在 操作台- 雇主- 全部雇用 中查看进程。"
        SpannableUtil.setSpannableColor(
            mTvSignSucccess,ResUtils.getColorRes(R.color.color_E26853),text,spannText)
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