package com.flash.worker.lib.pay

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alipay.sdk.app.AuthTask
import com.alipay.sdk.app.PayTask
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.WxPayInfo
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.pay.data.ali.AuthResult
import com.flash.worker.lib.pay.data.ali.PayResult
import com.flash.worker.lib.pay.interfaces.OnPayCompleteListener
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayHelper
 * Author: Victor
 * Date: 2020/11/30 18:32
 * Description: 支付模块
 * -----------------------------------------------------------------
 */
class PayHelper(var activity: AppCompatActivity?, var listener: OnPayCompleteListener?) {
    companion object {
        val WX_PAY: Int = 0x101
        val ALI_PAY: Int = 0x102
        val BIND_ALI_PAY: Int = 0x103
    }

    private val TAG = "PayHelper"
    private var mPayHandler: Handler? = null
    private var mPayHandlerThread: HandlerThread? = null

    private var mIWxApi: IWXAPI? = null

    init {
        subscribeEvent()
        startPayTask()
    }

    fun subscribeEvent() {
        LiveDataBus.with(Constant.Msg.WXPAY_RESULT)
            .observe(activity!!, Observer {
                if (it is PayResp) {
                    onWxPaySuccess(it)
                }
        })
    }

    private fun startPayTask() {
        mPayHandlerThread = HandlerThread("payTask")
        mPayHandlerThread?.start()
        mPayHandler = object : Handler(mPayHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    WX_PAY -> {
                        val wxPayMap =
                                msg.obj as HashMap<Int, Any>
                        val wxPayInfo = wxPayMap[WX_PAY] as WxPayInfo
                        wxPay(wxPayInfo)
                    }
                    ALI_PAY -> {
                        val aliPayMap =
                            msg.obj as HashMap<Int, Any>
                        val orderInfo = aliPayMap[ALI_PAY] as String?
                        aliPay(activity, orderInfo)
                    }
                    BIND_ALI_PAY -> {
                        val aliPayMap =
                            msg.obj as HashMap<Int, Any>
                        val orderInfo = aliPayMap[BIND_ALI_PAY] as String?
                        bindAliPay(activity, orderInfo)
                    }

                }
            }
        }
    }

    fun sendPayRequestWithParms(action: Int, requestData: Any) {
        val requestMap = HashMap<Int, Any>()
        requestMap[action] = requestData
        val msg = mPayHandler?.obtainMessage(action, requestMap)
        mPayHandler?.sendMessage(msg)
    }

    fun onDestroy() {
        mPayHandlerThread?.quit()
        mPayHandlerThread = null
    }

    /**
     * 0 支付成功
     * -1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
     * -2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
     */
    fun onWxPaySuccess(result: PayResp) {
        when (result.errCode) {
            0 -> onPayResult("支付成功", true)
            -1 -> onPayResult("支付异常", false)
            -2 -> onPayResult("用户中途取消支付", false)
            else -> onPayResult("支付失败", false)
        }
    }

    fun bindAliPay (activity: Activity?,authInfo: String?) {
        Loger.e(TAG,"bindAliPay-authInfo = " + authInfo)
        // 构造AuthTask 对象
        val authTask = AuthTask(activity)
        // 获取授权结果。
        val result = authTask.authV2(authInfo, true)
        val authResult = AuthResult(result,true)

        val resultStatus = authResult.getResultStatus()

        Loger.e(TAG,"bindAliPay-authResult = " + JsonUtils.toJSONString(authResult))

        // 判断resultStatus 为“9000”且result_code
        // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
            // 获取alipay_open_id，调支付时作为参数extern_token 的value
            // 传入，则支付账户为该授权账户
            onAuthResult(authResult.getAuthCode(),true)
        } else {
            // 其他状态值则为授权失败
            onAuthResult("支付宝授权失败",false)
        }

    }

    /**支付宝支付
     * @param activity
     * @param orderInfo 服务器返回的经过加签验证的字符串码(orderInfo 的获取必须来源于服务端)
     */
    fun aliPay(activity: Activity?,orderInfo: String?) {
        val alipay = PayTask(activity)
        val result = alipay.payV2(orderInfo, true)
        val payResult = PayResult(result)

        /**
         * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        val resultInfo: String = payResult.getResult()!! // 同步返回需要验证的信息
        val resultStatus: String = payResult.getResultStatus()!!
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            onPayResult("支付成功-$resultInfo", true)
        } else if (TextUtils.equals(resultStatus, "8000")) {
            onPayResult("正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态", false)
        } else if (TextUtils.equals(resultStatus, "4000")) {
            onPayResult("订单支付失败", false)
        } else if (TextUtils.equals(resultStatus, "5000")) {
            onPayResult("重复请求", false)
        } else if (TextUtils.equals(resultStatus, "6001")) {
            onPayResult("用户中途取消支付", false)
        } else if (TextUtils.equals(resultStatus, "6004")) {
            onPayResult("网络连接出错", false)
        } else if (TextUtils.equals(resultStatus, "6002")) {
            onPayResult("支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态", false)
        } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            onPayResult("支付失败", false)
        }
    }

    /**
     * 微信支付
     */
    private fun wxPay(bean: WxPayInfo) {
        Loger.e(TAG, "wxPay()......bean = " + JsonUtils.toJSONString(bean))
        if (TextUtils.isEmpty(bean.appid)) {
            bean.appid = AppConfig.WECHAT_APP_ID
        }
        mIWxApi = getIWXAPI()
        if (!AppUtil.isWXAppInstalledAndSupported(mIWxApi!!)) {
            onPayResult(ResUtils.getStringRes(R.string.not_install_wx_tip),false)
            return
        }
        val req = PayReq()
        req.appId = bean.appid
        req.partnerId = bean.partnerid
        req.prepayId = bean.prepayid
        req.nonceStr = bean.noncestr
        req.timeStamp = bean.timestamp
        req.packageValue = bean.package_
        req.sign = bean.sign
        mIWxApi?.sendReq(req)
    }

    private fun getIWXAPI(): IWXAPI? {
        if (mIWxApi == null) {
            mIWxApi = WXAPIFactory.createWXAPI(App.get(), AppConfig.WECHAT_APP_ID, true)
            mIWxApi?.registerApp(AppConfig.WECHAT_APP_ID)
        }
        return mIWxApi
    }

    /**
     * 处理返回结果
     * @param msg
     * @param isPaySuccess
     */
    private fun onPayResult(msg: String, isPaySuccess: Boolean) {
        MainHandler.get().runMainThread(Runnable {
            listener?.OnPlayComplete(msg, isPaySuccess)
        })
    }
    /**
     * 处理授权结果
     * @param msg
     * @param isAuthSuccess
     */
    private fun onAuthResult(msg: String?, isAuthSuccess: Boolean) {
        MainHandler.get().runMainThread(Runnable {
            listener?.OnAuthComplete(msg, isAuthSuccess)
        })
    }

}