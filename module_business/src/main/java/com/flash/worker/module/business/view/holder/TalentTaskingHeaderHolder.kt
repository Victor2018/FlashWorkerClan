package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_tasking_header_cell.view.*
import kotlinx.android.synthetic.main.rv_talent_tasking_header_p_cell.view.*
import kotlinx.android.synthetic.main.rv_talent_tasking_header_s_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitPrepaidHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskingHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData () {
        itemView.mTvParentTitle.setOnClickListener(this)

       /* if (TextUtils.isEmpty(spannText)) {
            itemView.mTvTip.text = text
        } else {
            SpannableUtil.setSpannableColor(
                    itemView.mTvTip, ResUtils.getColorRes(R.color.color_E26853),text,spannText)
        }*/

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        Loger.e(javaClass.simpleName,"onClick------>")
        when (v?.id) {
            R.id.mTvParentTitle -> {
                if (itemView.mElWaitPrepaid.isExpanded) {
                    itemView.mElWaitPrepaid.collapse()
                } else {
                    itemView.mElWaitPrepaid.expand()
                }
            }
        }
    }
}