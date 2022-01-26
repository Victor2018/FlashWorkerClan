package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.etfilter.PointLengthFilter
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.PayData
import com.flash.worker.lib.coremodel.data.parm.PayParm
import com.flash.worker.lib.coremodel.data.parm.PayQueryParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.PayVM
import com.flash.worker.lib.pay.PayHelper
import com.flash.worker.lib.pay.data.PayChannel
import com.flash.worker.lib.pay.interfaces.OnPayCompleteListener
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.PayWayAdapter
import com.flash.worker.module.mine.view.data.PayWayInfo
import kotlinx.android.synthetic.main.activity_recharge.*

@Route(path = ARouterPath.RechargeAct)
class RechargeActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener,
        OnPayCompleteListener,TextView.OnEditorActionListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, RechargeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val payVM: PayVM by viewModels {
        InjectorUtils.providePayVMFactory(this)
    }

    var mPayWayAdapter: PayWayAdapter? = null
    var mLoadingDialog: LoadingDialog? = null
    var mPayHelper: PayHelper? = null
    var mPayData: PayData? = null
    var requestPayQueryCount: Int = 0

    override fun getLayoutResource() = R.layout.activity_recharge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mPayHelper = PayHelper(this,this)

        mPayWayAdapter = PayWayAdapter(this,this)
        mRvPayWay.adapter = mPayWayAdapter

        mIvBack.setOnClickListener(this)
        mTvRecharge.setOnClickListener(this)

        mEtRechargeAmount.setOnEditorActionListener(this)

        mEtRechargeAmount.filters = arrayOf(PointLengthFilter(5,2))

        setSpannable()
    }

    fun initData () {
        var payWayDatas = ArrayList<PayWayInfo>()

        var payWayTitles = ResUtils.getStringArrayRes(R.array.mine_pay_way_titles)

        var count = payWayTitles?.size ?: 0
        for (i in 0 until count) {
            var data = PayWayInfo()
            data.payWayName = payWayTitles?.get(i)
            data.payWayIconResId = resources.obtainTypedArray(R.array.mine_pay_way_icons).getResourceId(i,0)
            payWayDatas.add(data)
        }

        mPayWayAdapter?.clear()
        mPayWayAdapter?.add(payWayDatas)
        mPayWayAdapter?.notifyDataSetChanged()
    }

    private fun setSpannable() {
        var tip = "提示：因 微信支付 对充值额度有限制，若数额较大建议使用支付宝进行充值！谢谢您的理解和支持！"
        val redTxt = "微信支付"
        val mColorBase: Int = ResUtils.getColorRes(R.color.color_059504)
        val mRedCount = getWordCount(redTxt)
        val spannableString = SpannableString(tip)

        var redIndex = 0
        while (redIndex != -1) {
            redIndex = tip.indexOf(redTxt, redIndex)
            if (redIndex == -1) break
            spannableString.setSpan(
                ForegroundColorSpan(mColorBase),
                redIndex,
                (redIndex + mRedCount).also { redIndex = it },
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        mTvPayTip.setText(spannableString)
    }

    fun getWordCount(s: String): Int {
        var s = s
        s = s.replace("[\\u4e00-\\u9fa5]".toRegex(), "*")
        return s.length
    }

    fun subscribeUi () {
        payVM.payData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mPayData = it.value.data

                    mLoadingDialog?.show()
                    if (TextUtils.equals(PayChannel.ALI_PAY,mPayData?.channel)) {
                        mPayHelper?.sendPayRequestWithParms(PayHelper.ALI_PAY,it.value.data?.alipayResult?.body!!)
                    } else if (TextUtils.equals(PayChannel.WX_PAY,mPayData?.channel)) {
                        mPayHelper?.sendPayRequestWithParms(PayHelper.WX_PAY,it.value.data?.weixinResult!!)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        payVM.payQueryData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (it.value?.data?.tradeStatus == 2) {
                        ToastUtils.show("充值成功")

                        if (mPayWayAdapter?.checkPosition == 0) {
                            UMengEventModule.report(this,MineEvent.recharge_from_zfb)
                        } else if (mPayWayAdapter?.checkPosition == 1) {
                            UMengEventModule.report(this,MineEvent.recharge_from_wechat)
                        }

                        onBackPressed()
                    } else {
                        if (requestPayQueryCount < 3) {
                            sendPayQueryRequest()
                            requestPayQueryCount++
                        } else {
                            ToastUtils.show(it.value.message)
                        }
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendPayRequest (payWay: Int,payAmt: Double) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = PayParm()
        if (payWay == PayHelper.ALI_PAY) {
            body.channel = PayChannel.ALI_PAY
        } else if (payWay == PayHelper.WX_PAY) {
            body.channel = PayChannel.WX_PAY
        }
        body.amount = payAmt
        payVM.pay(token,body)
    }

    fun sendPayQueryRequest () {
        if (mPayData == null) {
            ToastUtils.show("支付订单异常")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = PayQueryParm()
        body.channel = mPayData?.channel
        body.outTradeNo = mPayData?.outTradeNo
        payVM.payQuery(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvRecharge -> {
                rechargeAction()
            }
        }
    }

    fun rechargeAction () {
        var payAmt = mEtRechargeAmount.text.toString()
        if (TextUtils.isEmpty(payAmt)) {
            ToastUtils.show("请输入充值金额")
            return
        }
        if (mPayWayAdapter?.checkPosition == 0) {
            sendPayRequest(PayHelper.ALI_PAY,payAmt.toDouble())
        } else if (mPayWayAdapter?.checkPosition == 1) {
            sendPayRequest(PayHelper.WX_PAY,payAmt.toDouble())
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mPayWayAdapter?.checkPosition = position
        mPayWayAdapter?.notifyDataSetChanged()
    }

    override fun OnPlayComplete(msg: String?, isPaySuccess: Boolean) {
        mLoadingDialog?.dismiss()
        if (!isPaySuccess) {
            ToastUtils.show(msg)
            return
        }
        sendPayQueryRequest()
    }

    override fun OnAuthComplete(msg: String?, isAuthSuccess: Boolean) {
    }


    override fun onDestroy() {
        super.onDestroy()
        mPayHelper?.onDestroy()
        mPayHelper = null
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            rechargeAction()
            return true
        }
        return false
    }

}