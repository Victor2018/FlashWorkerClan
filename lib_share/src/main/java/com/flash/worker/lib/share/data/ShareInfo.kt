package com.flash.worker.lib.share.data

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareInfo
 * Author: Victor
 * Date: 2020/11/30 19:26
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareInfo: Serializable {
    var cover: String? = null

    var title: String? = null

    var url: String? = null

    var summary: String? = null
}