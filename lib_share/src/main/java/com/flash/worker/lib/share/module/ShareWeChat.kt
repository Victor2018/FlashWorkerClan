package com.flash.worker.lib.share.module

import android.text.TextUtils
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.share.R
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareItem
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.flash.worker.lib.common.util.ResUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareWeChat
 * Author: Victor
 * Date: 2020/11/30 19:28
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareWeChat(var mShareType:Int): IShareItem {
    private var mWxApi: IWXAPI? = null

    private var mShareListener: IShareListener? = null

    private var mTransaction: String? = null

    override fun initShare(listener: IShareListener?) {
        mShareListener = listener
        //微信分享初始化
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(App.get(), AppConfig.WECHAT_APP_ID, true)
            mWxApi?.registerApp(AppConfig.WECHAT_APP_ID)
        }
    }


    override fun invokeShare(shareInfo: ShareInfo?, image: ShareImage?) {
        if (shareInfo == null) return

        if (mWxApi != null) {
            if (!AppUtil.isWXAppInstalledAndSupported(mWxApi!!)) {
                mShareListener?.onShareError(getShareType(),ResUtils.getStringRes(R.string.not_install_wx_tip))
                return
            }
        }

        val webPage = WXWebpageObject()
        webPage.webpageUrl = shareInfo.url
        val msg = WXMediaMessage(webPage)
        msg.title = shareInfo.title
        msg.description = shareInfo.summary

        if (image != null) {
            msg.setThumbImage(image.getImage())
        }
        //构造一个Req
        val req = SendMessageToWX.Req()
        //用于唯一标识一个请求
        mTransaction = System.currentTimeMillis().toString()
        req.transaction = mTransaction
        req.message = msg
        req.scene = getWeChatShareType()
        mWxApi?.sendReq(req)
    }

    /**
     * 分享结果返回
     *
     * @param code
     * @param transaction
     */
    fun onShareResult(code: Int, transaction: String?) {
        if (mShareListener == null) return
        if (!TextUtils.equals(transaction, mTransaction)) return
        when (code) {
            BaseResp.ErrCode.ERR_OK -> {
                mShareListener?.onShareSuccess(getShareType())
                return
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> invokeShareFailed(R.string.notify_share_cancel)
            BaseResp.ErrCode.ERR_AUTH_DENIED -> invokeShareFailed(R.string.notify_share_auth_denied)
            else -> invokeShareFailed(R.string.notify_share_failed)
        }
    }

    private fun invokeShareFailed(textRes: Int) {
        val notify: String = ResUtils.getStringRes(textRes)
        mShareListener?.onShareCancel(mShareType, notify)
    }

    private fun getWeChatShareType(): Int {
        return if (mShareType == IShareType.SHARE_WX) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
    }

    override fun getShareType(): Int {
        return mShareType
    }

    override fun onDestroy() {
        mTransaction = null
        mShareListener = null
    }
}