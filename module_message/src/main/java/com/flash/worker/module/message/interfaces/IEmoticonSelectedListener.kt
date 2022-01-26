package com.flash.worker.module.message.interfaces


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmoticonSelectedListener
 * Author: Victor
 * Date: 2021/4/27 14:24
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmoticonSelectedListener {
    fun onEmojiSelected(key: String?)

    fun onStickerSelected(
        categoryName: String?,
        stickerName: String?
    )
}