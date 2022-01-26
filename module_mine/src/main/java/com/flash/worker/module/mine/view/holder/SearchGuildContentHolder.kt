package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SearchGuildInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_search_guild_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchGuildContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchGuildContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SearchGuildInfo?) {
        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic, R.mipmap.ic_president_avatar)
        itemView.mTvGuildName.text = data?.guildName
        itemView.mTvOwnerUsername.text = "会长: ${data?.ownerUsername}"

        var peopleNum = data?.peopleNum ?: 0
        if (peopleNum < 100) {
            itemView.mTvPeopleCount.text = "人数：100以内"
        } else {
            itemView.mTvPeopleCount.text = "人数：$peopleNum"
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}