package com.flash.worker.lib.share.module

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareController
import com.flash.worker.lib.share.interfaces.IShareItem
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.flash.worker.lib.livedatabus.core.LiveDataBus


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareController
 * Author: Victor
 * Date: 2020/11/30 19:25
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareController(): IShareController {
    private val TAG = "ShareController"
    private var mShareInfo: ShareInfo? = null
    private var mShareItem: IShareItem? = null
    private var mShareImage: ShareImage? = null
    private var mShareListener: IShareListener? = null

    fun subscribeEvent() {
        LiveDataBus.with(Constant.Msg.SHARE_WECHAT_RESULT)
            .observe(mShareListener?.getAttachActivity()!!, Observer {
                if (it is BaseResp) {
                    onShareSubscribe(it)
                }
            })
    }

    fun initShare(): ShareController? {
        return ShareController()
    }

    override fun setShareInfo(info: ShareInfo?) {
        mShareInfo = info
    }

    override fun setShareImage(image: ShareImage?) {
        mShareImage = image
    }

    override fun setShareListener(iShareListener: IShareListener?) {
        mShareListener = iShareListener
        subscribeEvent()
    }

    override fun invokeShare(activity: Activity?, shareType: Int) {
        mShareItem = getShareItem(shareType)
        if (mShareItem == null) return
        mShareItem?.initShare(mShareListener)
        mShareItem?.invokeShare(mShareInfo, mShareImage)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (mShareItem is ShareTencent) {
            val item = mShareItem as ShareTencent
            item.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        mShareItem?.onDestroy()
        mShareItem = null
        mShareListener = null
        mShareImage = null
        mShareInfo = null
    }

    /**
     * 微信分享成功回调
     *
     * @param bean
     */
    fun onShareSubscribe(bean: BaseResp) {
        Loger.e(TAG, "onShareSubscribe()......bean = " + JsonUtils.toJSONString(bean))
        if (mShareItem is ShareWeChat) {
            val item: ShareWeChat? = mShareItem as ShareWeChat?
            item?.onShareResult(bean.errCode, bean.transaction)
        }
    }


    /**
     * 获取分享类型
     *
     * @param iShareType
     * @return
     */
    private fun getShareItem(iShareType: Int): IShareItem? {
        return when (iShareType) {
            IShareType.SHARE_WX, IShareType.SHARE_FRIENDS -> ShareWeChat(iShareType)
            IShareType.SHARE_QQ, IShareType.SHARE_QZONE -> ShareTencent(iShareType)
            else -> ShareWeChat(IShareType.SHARE_WX)
        }
    }

}