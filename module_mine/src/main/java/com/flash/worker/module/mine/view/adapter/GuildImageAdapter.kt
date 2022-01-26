package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.GuildImageContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildImageAdapter
 * Author: Victor
 * Date: 2021/5/19 10:28
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildImageAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<WorkPicInfo, RecyclerView.ViewHolder>(context, listener)  {

    var isPreview: Boolean = false

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GuildImageContentHolder(inflate(R.layout.rv_guild_image_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
        val contentViewHolder = viewHolder as GuildImageContentHolder

        contentViewHolder.bindData(context,data,isPreview)

        contentViewHolder.mOnItemClickListener = listener
    }
}