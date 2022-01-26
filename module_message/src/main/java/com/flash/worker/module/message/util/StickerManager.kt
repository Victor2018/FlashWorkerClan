package com.flash.worker.module.message.util

import android.content.res.AssetManager
import android.util.Log
import com.flash.worker.lib.common.app.App
import com.flash.worker.module.message.data.StickerCategory
import java.io.IOException
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickerManager
 * Author: Victor
 * Date: 2021/4/27 12:18
 * Description: 
 * -----------------------------------------------------------------
 */
class StickerManager {
    private val TAG = "StickerManager"

    private val CATEGORY_AJMD = "ajmd"
    private val CATEGORY_XXY = "xxy"
    private val CATEGORY_LT = "lt"

    /**
     * 数据源
     */
    private val stickerCategories: MutableList<StickerCategory> = ArrayList()
    private val stickerCategoryMap: MutableMap<String, StickerCategory> = HashMap()
    private val stickerOrder: MutableMap<String, Int?> = HashMap(3)

    companion object {
        private var instance: StickerManager? = null
        fun getInstance(): StickerManager {
            if (instance == null) {
                instance = StickerManager()
            }
            return instance!!
        }
    }

    constructor() {
        initStickerOrder()
        loadStickerCategory()
    }

    fun init() {
        Log.i(TAG, "Sticker Manager init...")
    }

    private fun initStickerOrder() {
        // 默认贴图顺序
        stickerOrder[CATEGORY_AJMD] = 1
        stickerOrder[CATEGORY_XXY] = 2
        stickerOrder[CATEGORY_LT] = 3
    }

    private fun isSystemSticker(category: String): Boolean {
        return CATEGORY_XXY == category || CATEGORY_AJMD == category || CATEGORY_LT == category
    }

    private fun getStickerOrder(categoryName: String): Int {
        return if (stickerOrder.containsKey(categoryName)) {
            stickerOrder[categoryName]!!
        } else {
            100
        }
    }

    private fun loadStickerCategory() {
        val assetManager: AssetManager = App.get().resources.assets
        try {
            val files = assetManager.list("sticker")
            var category: StickerCategory
            for (name in files!!) {
                if (!hasExtentsion(name)) {
                    category = StickerCategory(name, name, true, getStickerOrder(name))
                    stickerCategories.add(category)
                    stickerCategoryMap[name] = category
                }
            }
            // 排序
            Collections.sort(
                stickerCategories,
                Comparator { l, r -> l.getOrder() - r.getOrder() })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getCategories(): List<StickerCategory>? {
        return stickerCategories
    }

    @Synchronized
    fun getCategory(name: String?): StickerCategory? {
        return stickerCategoryMap.get(name)
    }

    fun hasExtentsion(filename: String): Boolean {
        val dot = filename.lastIndexOf('.')
        return dot > -1 && dot < filename.length - 1
    }

}