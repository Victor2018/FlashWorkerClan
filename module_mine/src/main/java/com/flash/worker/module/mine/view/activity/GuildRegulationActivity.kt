package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_guild_regulation.*

class GuildRegulationActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,regulation: String?) {
            var intent = Intent(activity, GuildRegulationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,regulation)
            activity.startActivity(intent)
        }
    }

    var regulation: String? = null

    override fun getLayoutResource() = R.layout.activity_guild_regulation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
        initData(intent)
    }

    fun initialzie () {
        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        regulation = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        mTvRegulation.text = regulation
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}