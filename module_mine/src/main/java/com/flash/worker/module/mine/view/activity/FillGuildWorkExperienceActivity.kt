package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_fill_guild_work_experience.*

class FillGuildWorkExperienceActivity : BaseActivity(),View.OnClickListener, TextWatcher {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: String?) {
            var intent = Intent(activity, FillGuildWorkExperienceActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var workExperience: String? = null

    override fun getLayoutResource() = R.layout.activity_fill_guild_work_experience

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mEtWorkExperience.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        workExperience = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        mEtWorkExperience.setText(workExperience)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                var workExperience = mEtWorkExperience.text.toString()
                LiveDataBus.send(MineActions.UPDATE_WORKING_EXPERIENCE,workExperience)
                onBackPressed()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvReportContentCount.text = "${s?.length}/100"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}