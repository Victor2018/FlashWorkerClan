package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.UserCouponInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_coupon_detail.*

class CouponDetailActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: UserCouponInfo?) {
            var intent = Intent(activity, CouponDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null
    var mUserCouponInfo: UserCouponInfo? = null

    override fun getLayoutResource() = R.layout.activity_coupon_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
    }
    
    fun initData (intent: Intent?) {
        mUserCouponInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as UserCouponInfo
        mTvName.text = mUserCouponInfo?.name
        mTvExpireTime.text = "${mUserCouponInfo?.expireTime}\t\t到期"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}