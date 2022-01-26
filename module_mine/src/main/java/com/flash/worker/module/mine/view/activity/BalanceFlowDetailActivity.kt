package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
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
import kotlinx.android.synthetic.main.activity_balance_flow_detail.*

@Route(path = ARouterPath.BalanceFlowDetailAct)
class BalanceFlowDetailActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: BalanceFlowInfo?) {
            var intent = Intent(activity, BalanceFlowDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var mBalanceFlowInfo: BalanceFlowInfo? = null

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_balance_flow_detail

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

        mTvTitle.text = mBalanceFlowInfo?.bizName
        when (mBalanceFlowInfo?.bizType) {
            60 -> {//报酬支出
                mTvBizName.text = "结算金额"
            }
            85 -> {//报酬赔付
                mTvBizName.text = "赔付金额"
            }
            110 -> {//雇用奖励
                mTvBizName.text = "奖励金额"
            }
            else -> {
                mTvBizName.text = mBalanceFlowInfo?.bizName
            }
        }
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
        var prefixStr = ""
        when (data?.data?.bizType) {
            60 -> {//报酬支出
                prefixStr = "-"
            }
            65 -> {//报酬收入
                prefixStr = "+"
            }
            70 -> {//平台服务费
                prefixStr = "-"
            }
            75 -> {//信用赔付
                prefixStr = "-"
            }
            80 -> {//信用获赔
                prefixStr = "+"
            }
            85 -> {//报酬赔付
                prefixStr = "-"
            }
            90 -> {//报酬获赔
                prefixStr = "+"
            }
            95 -> {//经费收入
                prefixStr = "+"
            }
            100 -> {//公会红包
                prefixStr = "+"
            }
            105 -> {//红包收入
                prefixStr = "+"
            }
            110 -> {//雇用奖励
                prefixStr = "+"
            }
        }
        mTvTradeAmount.text = "$prefixStr${AmountUtil.addCommaDots(data.data?.tradeAmount)}"
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