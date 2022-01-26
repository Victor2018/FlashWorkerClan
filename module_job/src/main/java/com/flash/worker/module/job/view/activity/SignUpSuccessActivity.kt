package com.flash.worker.module.job.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.module.job.R
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_sign_up.mIvBack
import kotlinx.android.synthetic.main.activity_sign_up_success.*

class SignUpSuccessActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SignUpSuccessActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_sign_up_success

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
        var spannText = "操作台- 人才- 待雇用"
        var text = "您可在 操作台- 人才- 待雇用 中查看进程。"
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