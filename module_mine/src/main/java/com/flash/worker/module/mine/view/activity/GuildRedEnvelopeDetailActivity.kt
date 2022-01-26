package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.BalanceFlowDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_funding_income_detail.*

class GuildRedEnvelopeDetailActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: BalanceFlowInfo?) {
            var intent = Intent(activity, GuildRedEnvelopeDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var mBalanceFlowInfo: BalanceFlowInfo? = null

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_guild_red_envelope_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mIvBack.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mBalanceFlowInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as BalanceFlowInfo?
        sendBalanceFlowaDetailRequest()
    }


    fun subscribeUi () {
        accountVM.balanceFlowDetailData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showBalanceFlowDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendBalanceFlowaDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchBalanceFlowDetail(token,mBalanceFlowInfo?.outTradeNo)
    }

    fun showBalanceFlowDetail (data: BalanceFlowDetailReq) {
        mTvTradeAmount.text = "+${AmountUtil.addCommaDots(data.data?.tradeAmount)}"
        mTvSalaryIncomeAmount.text = "${AmountUtil.addCommaDots(data.data?.actualAmount)}元"
        mTvCreateTime.text = data.data?.tradeTime
        mTvDescription.text = data.data?.bizDesc
        mTvTradeNo.text = data.data?.outTradeNo
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}