package com.flash.worker.lib.common.view.holder

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearSnapHelper
import com.flash.worker.lib.coremodel.data.bean.TalentInfo
import com.flash.worker.lib.common.view.adapter.TalentCellAdapter
import kotlinx.android.synthetic.main.rv_talent_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentInfo?, listener: AdapterView.OnItemClickListener?, checkCellId: String?) {
        itemView.mTvCellTitle.text = data?.name

        itemView.mRvTalentCell.setOnFlingListener(null)
        LinearSnapHelper().attachToRecyclerView(itemView.mRvTalentCell)

        var cellAdapter = TalentCellAdapter(itemView.context!!, listener, adapterPosition)
        cellAdapter.checkCellId = checkCellId
        cellAdapter.add(data?.childs)

        itemView.mRvTalentCell.adapter = cellAdapter

        //解决父view 垂直recyclerView与CoordinatorLayout 联动问题
        //此横向RecyclerView的父View是纵向RecyclerView，而RecyclerView只实现了NestedScrollingChild，
        // 无法像CoordinatorLayout一样响应。所以要关闭横向RecyclerView的嵌套滑动功能，
        // 让横向RecyclerView如同其他嵌入纵向RecyclerView的view一样，触发折叠。
        //链接：简书https://www.jianshu.com/p/ddbe6a4095d3
        itemView.mRvTalentCell.setNestedScrollingEnabled(false)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}