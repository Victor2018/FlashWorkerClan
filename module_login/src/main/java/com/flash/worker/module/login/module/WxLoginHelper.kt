package com.flash.worker.module.login.module

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.util.AppConfig
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.flash.worker.lib.livedatabus.core.LiveDataBus


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WxLoginHelper
 * Author: Victor
 * Date: 2020/12/8 15:34
 * Description: 
 * -----------------------------------------------------------------
 */
class WxLoginHelper(var activity: AppCompatActivity?) {
    val TAG = "WxLoginHelper"
    private var mIWxApi: IWXAPI? = null

    init {
        subscribeEvent()
    }

    fun subscribeEvent() {
        LiveDataBus.with(Constant.Msg.LOGIN_AUTH_WEIXIN)
            .observe(activity!!, Observer {
                if (it is SendAuth.Resp) {
                    Loger.e(TAG,"SendAuth.Resp = " + JsonUtils.toJSONString(it))
                }
            })
        LiveDataBus.with(Constant.Msg.WX_AUTH_ERROR)
            .observe(activity!!, Observer {
                ToastUtils.show("拒绝授权微信登录")
            })
    }

    fun wxLogin () {
        if (!getIWXAPI()?.isWXAppInstalled()!!) {
            ToastUtils.show("您还未安装微信客户端")
            return
        }
        var req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "sgz_wx_login"
        getIWXAPI()?.sendReq(req)
    }

    private fun getIWXAPI(): IWXAPI? {
        if (mIWxApi == null) {
            mIWxApi = WXAPIFactory.createWXAPI(App.get(), AppConfig.WECHAT_APP_ID, true)
            mIWxApi?.registerApp(AppConfig.WECHAT_APP_ID)
        }
        return mIWxApi
    }
}