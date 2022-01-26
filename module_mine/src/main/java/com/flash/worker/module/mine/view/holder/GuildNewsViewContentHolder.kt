package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildNewsInfo
import kotlinx.android.synthetic.main.rv_guild_news_view_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildNewsViewContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildNewsViewContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: GuildNewsInfo?,count : Int) {
        itemView.mTvContent.text = data?.content
        itemView.mTvReleaseTime.text = data?.releaseTime

        if (adapterPosition == 0) {
            itemView.line_top_dot.visibility = View.GONE
            itemView.line_bottom_dot.visibility = View.VISIBLE
        } else if (adapterPosition == count - 1) {
            itemView.line_top_dot.visibility = View.VISIBLE
            itemView.line_bottom_dot.visibility = View.GONE
        }  else {
            itemView.line_top_dot.visibility = View.VISIBLE
            itemView.line_bottom_dot.visibility = View.VISIBLE
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}