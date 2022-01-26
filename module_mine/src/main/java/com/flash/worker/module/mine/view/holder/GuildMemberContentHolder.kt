package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildMemberInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_guild_member_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildMemberContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildMemberContentHolder(itemView: View) : ContentViewHolder(itemView) {
    companion object {
        const val ONITEM_LONG_CLICK: Long = -1
        const val ONITEM_CLICK: Long = 0
        const val ONITEM_COPY_CLICK: Long = 1
    }

    fun bindData (data: GuildMemberInfo?) {
        itemView.mTvUserId.setOnClickListener(this)

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)

        if (data?.sex == 0) {
            itemView.mIvSex.setImageResource(R.mipmap.ic_mine_female)
        } else if (data?.sex == 1) {
            itemView.mIvSex.setImageResource(R.mipmap.ic_mine_male)
        } else {
            itemView.mIvSex.setImageResource(0)
        }

        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "ID:${data?.userId}"
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.mTvUserId -> {
                mOnItemClickListener?.onItemClick(null, view, adapterPosition, ONITEM_COPY_CLICK)
            }
            else -> {
                mOnItemClickListener?.onItemClick(null, view, adapterPosition, ONITEM_CLICK)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        mOnItemClickListener?.onItemClick(null, v, adapterPosition, ONITEM_LONG_CLICK)
        return false
    }
}