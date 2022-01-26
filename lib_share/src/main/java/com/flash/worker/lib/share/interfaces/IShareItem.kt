package com.flash.worker.lib.share.interfaces

import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IShareItem
 * Author: Victor
 * Date: 2020/11/30 19:21
 * Description: 
 * -----------------------------------------------------------------
 */
interface IShareItem {
    fun initShare(listener: IShareListener?)

    fun invokeShare(shareInfo: ShareInfo?, image: ShareImage?)

    fun getShareType(): Int

    fun onDestroy()
}