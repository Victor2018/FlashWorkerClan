package com.flash.worker.module.message.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.StickerCategory
import com.flash.worker.module.message.data.StickerItem
import com.flash.worker.module.message.util.MoonUtil
import com.flash.worker.module.message.view.widget.EmoticonView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickerAdapter
 * Author: Victor
 * Date: 2021/4/27 14:38
 * Description: 
 * -----------------------------------------------------------------
 */
class StickerAdapter(var context: Context?,
                     var category: StickerCategory?,
                     var startIndex: Int): BaseAdapter() {

    override fun getCount(): Int { //获取每一页的数量
        var size = category?.getStickers()?.size ?: 0
        var count: Int = size - startIndex
        count = Math.min(count, EmoticonView.STICKER_PER_PAGE)
        return count
    }

    override fun getItem(position: Int): Any? {
        return category?.getStickers()?.get(startIndex + position)
    }

    override fun getItemId(position: Int): Long {
        return (startIndex + position).toLong()
    }


    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val viewHolder: StickerViewHolder
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.nim_sticker_picker_view, null)
            viewHolder = StickerViewHolder()
            viewHolder.imageView =
                convertView.findViewById<View>(R.id.sticker_thumb_image) as ImageView
            viewHolder.descLabel =
                convertView.findViewById<View>(R.id.sticker_desc_label) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as StickerViewHolder
        }
        val index = startIndex + position
        if (index >= category?.getStickers()?.size!!) {
            return convertView
        }
        val sticker: StickerItem = category?.getStickers()?.get(index) ?: return convertView

        val imgPath: String = MoonUtil.getStickerPath(sticker.getCategory(), sticker.getName())!!
        Glide.with(context!!).load(imgPath).into(viewHolder.imageView!!)
        viewHolder.descLabel?.visibility = View.GONE
        return convertView
    }

    internal class StickerViewHolder {
        var imageView: ImageView? = null
        var descLabel: TextView? = null
    }
}