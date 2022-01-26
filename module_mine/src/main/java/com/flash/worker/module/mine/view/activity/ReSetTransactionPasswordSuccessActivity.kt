package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_set_transaction_pwd.*
import kotlinx.android.synthetic.main.activity_set_transaction_pwd.mIvBack

class ReSetTransactionPasswordSuccessActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ReSetTransactionPasswordSuccessActivity::class.java)
            activity.startActivity(intent)
        }
    }


    override fun getLayoutResource() = R.layout.activity_reset_transaction_pwd_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }


    fun initialzie () {
        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                onBackPressed()
            }
        }
    }


}