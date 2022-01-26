package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.activity_invite_talent_success.*

class InviteTalentSuccessActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,talentUserName: String?) {
            var intent = Intent(activity, InviteTalentSuccessActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,talentUserName)
            activity.startActivity(intent)
        }
    }

    var talentUserName: String? = null

    override fun getLayoutResource() = R.layout.activity_invite_talent_success

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
        talentUserName = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        var spannText = "操作台- 雇主- 已发邀请"
        var text = "您已成功向${talentUserName}发送邀请！\n您可在 操作台- 雇主- 已发邀请 \n中查看本次邀请。"
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}