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
import kotlinx.android.synthetic.main.activity_fill_guild_regulation.*

class FillGuildRegulationActivity : BaseActivity(),View.OnClickListener, TextWatcher {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: String) {
            var intent = Intent(activity, FillGuildRegulationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var regulation: String? = null

    override fun getLayoutResource() = R.layout.activity_fill_guild_regulation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mEtRegulation.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        regulation = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        mEtRegulation.setText(regulation)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                var regulation = mEtRegulation.text.toString()
                LiveDataBus.send(MineActions.UPDATE_GUILD_REGULATION,regulation)
                onBackPressed()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvRegulationCount.text = "${s?.length}/500"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}