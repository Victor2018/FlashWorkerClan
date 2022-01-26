package com.flash.worker.lib.share.interfaces

import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError





/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITenUiListener
 * Author: Victor
 * Date: 2021/11/12 10:51
 * Description: 
 * -----------------------------------------------------------------
 */
class ITenUiListener(var shareType: Int, var listener: IShareListener?): IUiListener {

    override fun onComplete(o: Any?) {
        listener?.onShareSuccess(shareType)
    }

    override fun onError(uiError: UiError?) {
        listener?.onShareError(shareType, "分享失败～")
    }

    override fun onCancel() {
        listener?.onShareCancel(shareType, "分享取消~")
    }

    override fun onWarning(p0: Int) {
//        listener?.onShareError(shareType, "分享失败～$p0")
    }
}