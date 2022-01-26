package com.sgz.flash.workers.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.coremodel.util.AppConfig
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.flash.worker.lib.livedatabus.core.LiveDataBus


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WXEntryActivity
 * Author: Victor
 * Date: 2020/12/1 9:58
 * Description: 微信授权
 * -----------------------------------------------------------------
 */
class WXEntryActivity:Activity(), IWXAPIEventHandler {
    val TAG = "WXEntryActivity"
    private val SHARE_WECHAT = "CONTANT_SHARE:wechat"

    private val SHARE_FRIENDS = "CONTANT_SHARE:flash"

    /**
     * 微信API
     */
    private var api: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, AppConfig.WECHAT_APP_ID, false)
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    /**
     * 微信发送请求到第三方应用时,会回调到该方法
     *
     * @param req
     */
    override fun onReq(req: BaseReq?) {
        Loger.d(TAG, "onReq()......")
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     *
     * @param resp
     */
    override fun onResp(resp: BaseResp) {
        Loger.e(TAG, "onResp()-resp = " + JsonUtils.toJSONString(resp))
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL ->{
                LiveDataBus.send(Constant.Msg.WX_AUTH_ERROR,resp)
            }
            BaseResp.ErrCode.ERR_OK -> if (resp is SendAuth.Resp) {
                Loger.e(TAG, "onResp()-LOGIN_AUTH_WEIXIN")
                //微信登陆
                LiveDataBus.send(Constant.Msg.LOGIN_AUTH_WEIXIN,resp)
            } else if (resp is SendMessageToWX.Resp) {
                Loger.e(this.javaClass.simpleName, "onResp()-SHARE_WECHAT_RESULT")
                //分享返回
                LiveDataBus.send(Constant.Msg.SHARE_WECHAT_RESULT,resp)
            }

        }
        finish()
    }
}