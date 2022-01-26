package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildNewsInfo
import kotlinx.android.synthetic.main.rv_guild_news_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildNewsContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildNewsContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: GuildNewsInfo?) {
        itemView.mTvContent.text = data?.content
        itemView.mTvReleaseTime.text = data?.releaseTime

        itemView.mIvMore.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}