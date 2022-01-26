package com.flash.worker.module.message.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.flash.worker.module.message.R
import com.flash.worker.module.message.util.EmojiManager
import com.flash.worker.module.message.view.widget.EmoticonView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmojiAdapter
 * Author: Victor
 * Date: 2021/4/27 14:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmojiAdapter(var context: Context?, var startIndex: Int): BaseAdapter() {
    override fun getCount(): Int {
        var count = EmojiManager.getDisplayCount() - startIndex + 1
        count = Math.min(count, EmoticonView.EMOJI_PER_PAGE + 1)
        return count
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return (startIndex + position).toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {

        var mView = View.inflate(context, R.layout.nim_emoji_item, null)
        val emojiThumb = mView.findViewById<View>(R.id.imgEmoji) as ImageView

        val count = EmojiManager.getDisplayCount()
        val index = startIndex + position
        if (position == EmoticonView.EMOJI_PER_PAGE || index == count) {
            emojiThumb.setBackgroundResource(R.mipmap.nim_emoji_del)
        } else if (index < count) {
            emojiThumb.setBackgroundDrawable(EmojiManager.getDisplayDrawable(context!!, index))
        }
        return mView
    }

}