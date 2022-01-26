package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.coremodel.data.bean.EmployerInfo
import kotlinx.android.synthetic.main.rv_my_employer_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyEmployerContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class MyEmployerContentHolder(itemView: View) : ContentViewHolder(itemView)  {

    fun bindData (data: EmployerInfo?) {
        itemView.mTvResumeName.text = data?.name
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}