package com.flash.worker.lib.share.interfaces

import android.app.Activity
import android.content.Intent
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IShareController
 * Author: Victor
 * Date: 2020/11/30 19:19
 * Description: 
 * -----------------------------------------------------------------
 */
interface IShareController {
    /**
     * 设置分享主题
     */
    fun setShareInfo(info: ShareInfo?)

    /**
     * 设置分享图片
     * @param image
     */
    fun setShareImage(image: ShareImage?)

    /**
     * 分享接口回调
     *
     * @param iShareListener
     */
    fun setShareListener(iShareListener: IShareListener?)

    /**
     * 数据返回
     */
    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    )

    /**
     * 销毁
     */
    fun onDestroy()

    /**
     * 开始分享
     */
    fun invokeShare(activity: Activity?, shareType: Int)
}