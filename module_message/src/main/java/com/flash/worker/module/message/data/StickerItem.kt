package com.flash.worker.module.message.data


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickerItem
 * Author: Victor
 * Date: 2021/4/27 10:19
 * Description: 
 * -----------------------------------------------------------------
 */
class StickerItem {

    private var category: String? = null //类别名
    private var name: String? = null

    constructor(category: String?, name: String?) {
        this.category = category
        this.name = name
    }

    fun getIdentifier(): String? {
        return "$category/$name"
    }

    fun getCategory(): String? {
        return category
    }

    fun getName(): String? {
        return name
    }

    override fun equals(o: Any?): Boolean {
        if (o != null && o is StickerItem) {
            val item = o
            return item.getCategory() == category && item.getName() == name
        }
        return false
    }
}