package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BannerInfo
 * Author: Victor
 * Date: 2021/6/21 10:36
 * Description: 
 * -----------------------------------------------------------------
 */
class BannerInfo {
    var imageUrl: String? = null//图片URL
    var bannerType: Int = 0//广告类型：1-人才；2-工单
    var jumpLinkUrl: String? = null//跳转链接URL
    var jumpLinkType: String? = null//跳转链接类型
}