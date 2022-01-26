package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.coremodel.data.bean.ViolationLabelInfo
import kotlinx.android.synthetic.main.rv_violation_header_cell.view.*
import kotlinx.android.synthetic.main.rv_violation_header_cell_p.view.*
import kotlinx.android.synthetic.main.rv_violation_header_cell_s.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentViolationHeaderHolder
 * Author: Victor
 * Date: 2021/7/8 10:19
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationHeaderHolder(itemView: View) : HeaderViewHolder(itemView) {

    fun bindData (data: ViolationLabelInfo?,isReportTalentViolation: Boolean) {
        itemView.mTvParentTitle.setOnClickListener(this)

        if (isReportTalentViolation) {
            itemView.mTvTip.text = "尊敬的雇主，若您在雇用进行中，发现人才有违规行为，可在工单列表内发起投诉，终止雇用，要求信用金赔偿！"
        } else {
            itemView.mTvTip.text = "尊敬的人才，若您在雇用进行中，发现雇主有违规行为，可在工单列表内发起举报，终止工作，要求信用金赔偿！"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvParentTitle -> {
                if (itemView.mElTip.isExpanded) {
                    itemView.mElTip.collapse()
                } else {
                    itemView.mElTip.expand()
                }
            }
        }
    }
}