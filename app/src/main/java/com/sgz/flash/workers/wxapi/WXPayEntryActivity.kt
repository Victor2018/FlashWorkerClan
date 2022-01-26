package com.sgz.flash.workers.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.coremodel.util.AppConfig
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.flash.worker.lib.livedatabus.core.LiveDataBus


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WXPayEntryActivity
 * Author: Victor
 * Date: 2020/12/1 10:06
 * Description: 
 * -----------------------------------------------------------------
 */
class WXPayEntryActivity: Activity(), IWXAPIEventHandler {
    private val TAG = "WXPayEntryActivity"

    private var api: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, AppConfig.WECHAT_APP_ID)
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq?) {}

    override fun onResp(resp: BaseResp) {
        Log.d(TAG, "errCode = " + resp.errCode + " errStr:" + resp.errStr)
        if (resp is PayResp) {
            LiveDataBus.send(Constant.Msg.WXPAY_RESULT,resp)
        }
        finish()
    }
}