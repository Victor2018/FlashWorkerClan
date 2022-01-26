package com.flash.worker.lib.luban

import com.luck.picture.lib.entity.LocalMedia


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnCompressListener
 * Author: Victor
 * Date: 2020/12/23 19:16
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnCompressListener {
    fun OnCompress(datas: List<LocalMedia>?, msg: String?)
}