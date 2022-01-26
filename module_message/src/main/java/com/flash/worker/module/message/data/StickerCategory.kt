package com.flash.worker.module.message.data

import android.content.Context
import android.content.res.AssetManager
import com.flash.worker.lib.common.app.App
import java.io.IOException
import java.io.InputStream
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickerCategory
 * Author: Victor
 * Date: 2021/4/27 10:18
 * Description: 
 * -----------------------------------------------------------------
 */
class StickerCategory {
    private val serialVersionUID = -81692490861539040L

    private var name: String? = null// 贴纸包名
    private var title: String? = null // 显示的标题
    private var system = false // 是否是系统内置表情
    private var order = 0 // 默认顺序


    @Transient
    private var stickers: List<StickerItem>? = null

    constructor(
        name: String?,
        title: String?,
        system: Boolean,
        order: Int
    ) {
        this.title = title
        this.name = name
        this.system = system
        this.order = order
        loadStickerData()
    }

    fun system(): Boolean {
        return system
    }

    fun setSystem(system: Boolean) {
        this.system = system
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getStickers(): List<StickerItem>? {
        return stickers
    }

    fun hasStickers(): Boolean {
        return stickers != null && stickers!!.size > 0
    }

    fun getCoverNormalInputStream(context: Context): InputStream? {
        val filename = name + "_s_normal.png"
        return makeFileInputStream(context, filename)
    }

    fun getCoverPressedInputStream(context: Context): InputStream? {
        val filename = name + "_s_pressed.png"
        return makeFileInputStream(context, filename)
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getCount(): Int {
        return if (stickers == null || stickers!!.isEmpty()) {
            0
        } else stickers!!.size
    }

    fun getOrder(): Int {
        return order
    }

    private fun makeFileInputStream(
        context: Context,
        filename: String
    ): InputStream? {
        try {
            if (system) {
                val assetManager =
                    context.resources.assets
                val path = "sticker/$filename"
                return assetManager.open(path)
            } else {
                // for future
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun loadStickerData(): List<StickerItem>? {
        val stickers: MutableList<StickerItem> =
            ArrayList()
        val assetManager: AssetManager = App.get().getResources().getAssets()
        try {
            val files = assetManager.list("sticker/$name")
            for (file in files!!) {
                stickers.add(StickerItem(name, file))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        this.stickers = stickers
        return stickers
    }

    override fun equals(o: Any?): Boolean {
        if (o == null || o !is StickerCategory) {
            return false
        }
        if (o === this) {
            return true
        }
        return o.getName() == getName()
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}