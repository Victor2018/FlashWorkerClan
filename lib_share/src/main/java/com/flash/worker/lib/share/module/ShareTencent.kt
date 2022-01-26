package com.flash.worker.lib.share.module

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.share.R
import com.tencent.tauth.Tencent
import com.tencent.connect.share.QzoneShare
import com.flash.worker.lib.share.data.ShareInfo
import com.tencent.connect.share.QQShare
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.interfaces.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareTencent
 * Author: Victor
 * Date: 2021/11/12 10:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareTencent(var mShareType: Int): IShareItem {
    private var mTenApi: Tencent? = null

    private var mShareListener: IShareListener? = null

    private var mTenUiListener: ITenUiListener? = null

    override fun initShare(listener: IShareListener?) {
        mShareListener = listener
        mTenUiListener = ITenUiListener(mShareType, listener)
        if (!AppUtil.isAppExist(App.get(),AppConfig.PACKAGE_QQ)) {
            mShareListener?.onShareError(getShareType(), ResUtils.getStringRes(R.string.notify_qq_client_not_exist))
            return
        }
        //qq分享初始化
        if (mTenApi == null) {
            mTenApi = Tencent.createInstance(AppConfig.QQ_APP_ID, App.get())
        }
    }

    override fun invokeShare(shareInfo: ShareInfo?, image: ShareImage?) {
        if (shareInfo == null) return
        if (mShareListener == null) return
        if (getShareType() === IShareType.SHARE_QQ) {
            shareWithQQ(shareInfo)
        } else {
            shareWithQzone(shareInfo)
        }
    }

    private fun shareWithQQ(shareInfo: ShareInfo) {
        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        //分享的图片URL
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.cover)
        //这条分享消息被好友点击后的跳转URL
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.url)
        //分享的标题.注:PARAM_TITLE,PARAM_IMAGE_URL,PARAM_SUMMARY不能全为空,最少必须有一个是有值的。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.title)
        //分享的消息摘要,最长50个字
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary?.replace("\n","\t\t\t\t"))
        mTenApi?.shareToQQ(mShareListener?.getAttachActivity(), params, mTenUiListener)
    }

    private fun shareWithQzone(shareInfo: ShareInfo) {
        val imgUrls: ArrayList<String?> = ArrayList()
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareInfo.title) //必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary?.replace("\n","\t\t\t\t")) //选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareInfo.url) //必填
        if (!TextUtils.isEmpty(shareInfo.cover)) {
            imgUrls.add(shareInfo.cover)
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrls)
        mTenApi?.shareToQzone(mShareListener?.getAttachActivity(), params, mTenUiListener)
    }

    override fun getShareType(): Int {
        return mShareType
    }

    override fun onDestroy() {
        mShareListener = null
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mTenUiListener)
    }


}