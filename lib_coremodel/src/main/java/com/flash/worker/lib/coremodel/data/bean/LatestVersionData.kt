package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LatestVersionData
 * Author: Victor
 * Date: 2021/6/2 11:31
 * Description: 
 * -----------------------------------------------------------------
 */
class LatestVersionData {
    var isUpdate: Boolean = false//是否更新
    var isForceUpdate: Boolean = false//是否强制更新
    var versionName: String? = null//版本号
    var appStoreUrl: String? = null//AppStore地址
    var appDownloadUrl: String? = null//APP下载地址
    var appMd5: String? = null//App MD5值
    var releaseNotes: String? = null//版本说明
    var releaseTime: String? = null//发布时间
}