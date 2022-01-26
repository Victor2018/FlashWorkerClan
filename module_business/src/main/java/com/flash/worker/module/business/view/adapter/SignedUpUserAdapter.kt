package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.SignedUpUserContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SignedUpAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class SignedUpUserAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentUserInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, TalentUserInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentUserInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SignedUpUserContentHolder (inflate(R.layout.rv_signed_up_user_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentUserInfo?, position: Int) {
        val contentViewHolder = viewHolder as SignedUpUserContentHolder

        contentViewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)

        contentViewHolder.mOnItemClickListener = listener
    }

    fun isItemChecked (data: TalentUserInfo?): Boolean {
        if (checkMap[data?.id] != null) {
            return true
        }
        return false
    }

}