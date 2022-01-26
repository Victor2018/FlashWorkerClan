package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_reward_receive_description.*

class RewardReceiveDescriptionActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, RewardReceiveDescriptionActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_reward_receive_description

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        mIvBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
        }
    }
}