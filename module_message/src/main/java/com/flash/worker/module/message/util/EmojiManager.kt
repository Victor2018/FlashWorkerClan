package com.flash.worker.module.message.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.LruCache
import android.util.Xml
import com.flash.worker.lib.common.app.App
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmojiManager
 * Author: Victor
 * Date: 2021/4/27 12:25
 * Description: 
 * -----------------------------------------------------------------
 */
object EmojiManager {
    private const val EMOT_DIR = "emoji/"

    // max cache size
    private const val CACHE_MAX_SIZE = 1024

    private var pattern = Pattern.compile(patternOfDefault())

    // default entries
    private val defaultEntries: MutableList<Entry> = ArrayList()

    // text to entry
    private val text2entry: MutableMap<String, Entry> =
        HashMap()

    // asset bitmap cache, key: asset path
    private var drawableCache: LruCache<String, Bitmap>? = null

    init {
        load(App.get(), EMOT_DIR + "emoji.xml")
        drawableCache = object : LruCache<String, Bitmap>(CACHE_MAX_SIZE) {
            override fun entryRemoved(
                evicted: Boolean,
                key: String?,
                oldValue: Bitmap?,
                newValue: Bitmap?
            ) {
                super.entryRemoved(evicted, key, oldValue, newValue)
                if (oldValue != newValue) oldValue?.recycle()
            }
        }
    }

    private class Entry internal constructor(var text: String, var assetPath: String)

    //
    // display
    //

    //
    // display
    //
    fun getDisplayCount(): Int {
        return defaultEntries.size
    }

    fun getDisplayDrawable(
        context: Context,
        index: Int
    ): Drawable? {
        val text =
            if (index >= 0 && index < defaultEntries.size) defaultEntries[index].text else null
        return text?.let { getDrawable(context, it) }
    }

    fun getDisplayText(index: Int): String? {
        return if (index >= 0 && index < defaultEntries.size) defaultEntries[index].text else null
    }

    fun getPattern(): Pattern {
        return pattern
    }

    fun getDrawable(
        context: Context,
        text: String?
    ): Drawable? {
        val entry = text2entry[text] ?: return null
        var cache = drawableCache!![entry.assetPath]
        if (cache == null) {
            cache = loadAssetBitmap(context, entry.assetPath)
        }
        return BitmapDrawable(context.resources, cache)
    }

    private fun patternOfDefault(): String? {
        return "\\[[^\\[]{1,10}\\]"
    }

    private fun loadAssetBitmap(
        context: Context,
        assetPath: String
    ): Bitmap? {
        var `is`: InputStream? = null
        try {
            val resources = context.resources
            val options =
                BitmapFactory.Options()
            options.inDensity = DisplayMetrics.DENSITY_HIGH
            options.inScreenDensity = resources.displayMetrics.densityDpi
            options.inTargetDensity = resources.displayMetrics.densityDpi
            `is` = context.assets.open(assetPath)
            val bitmap =
                BitmapFactory.decodeStream(`is`, Rect(), options)
            if (bitmap != null) {
                drawableCache!!.put(assetPath, bitmap)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    private fun load(context: Context, xmlPath: String) {
        EntryLoader().load(context, xmlPath)
    }

    //
    // load emoticons from asset
    //
    private class EntryLoader : DefaultHandler() {
        private var catalog = ""
        fun load(context: Context, assetPath: String?) {
            var `is`: InputStream? = null
            try {
                `is` = context.assets.open(assetPath!!)
                Xml.parse(`is`, Xml.Encoding.UTF_8, this)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SAXException) {
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        @Throws(SAXException::class)
        override fun startElement(
            uri: String,
            localName: String,
            qName: String,
            attributes: Attributes
        ) {
            if (localName == "Catalog") {
                catalog = attributes.getValue(uri, "Title")
            } else if (localName == "Emoticon") {
                val tag = attributes.getValue(uri, "Tag")
                val fileName = attributes.getValue(uri, "File")
                val entry = Entry(tag, "$EMOT_DIR$catalog/$fileName")
                text2entry[entry.text] = entry
                if (catalog == "default") {
                    defaultEntries.add(entry)
                }
            }
        }
    }
}