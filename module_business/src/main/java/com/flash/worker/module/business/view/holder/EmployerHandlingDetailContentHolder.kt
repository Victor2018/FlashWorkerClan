package com.flash.worker.module.business.view.holder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.DisputeProgressInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_handling_detail_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerHandlingDetailContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerHandlingDetailContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: DisputeProgressInfo?,count: Int) {
        itemView.mTvMessage.setOnClickListener(this)

        itemView.mTvMessage.text = data?.message
        itemView.mTvCreateTime.text = data?.createTime

        if (count > 1) {
            if (adapterPosition == 0) {
                itemView.line_top_dot.visibility = View.GONE
                itemView.line_bottom_dot.visibility = View.VISIBLE
                itemView.iv_dot.setImageResource(R.drawable.bg_dot_focus)

                itemView.mTvMessage.setTextColor(ResUtils.getColorRes(R.color.color_333333))
                itemView.mTvMessage.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            } else if (adapterPosition == count - 1) {
                itemView.line_top_dot.visibility = View.VISIBLE
                itemView.line_bottom_dot.visibility = View.GONE
                itemView.iv_dot.setImageResource(R.drawable.bg_dot_normal)

                itemView.mTvMessage.setTextColor(ResUtils.getColorRes(R.color.color_666666))
                itemView.mTvMessage.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            } else {
                itemView.line_top_dot.visibility = View.VISIBLE
                itemView.line_bottom_dot.visibility = View.VISIBLE
                itemView.iv_dot.setImageResource(R.drawable.bg_dot_normal)

                itemView.mTvMessage.setTextColor(ResUtils.getColorRes(R.color.color_666666))
                itemView.mTvMessage.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }
        } else {
            itemView.line_top_dot.visibility = View.GONE
            itemView.line_bottom_dot.visibility = View.GONE
            itemView.iv_dot.setImageResource(R.drawable.bg_dot_focus)

            itemView.mTvMessage.setTextColor(ResUtils.getColorRes(R.color.color_333333))
            itemView.mTvMessage.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }

        if (data?.status == 10) {//平台介入中添加问号
            /*val spanString: Spannable = SpannableStringBuilder(data?.message + "\t ")
            val span = ImageSpan(itemView.context, R.mipmap.ic_dispute_question_mark, ImageSpan.ALIGN_BASELINE)
            // 用ImageSpan替换文本
            spanString.setSpan(span, spanString.length - 1, spanString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            itemView.mTvMessage.text = spanString*/

            itemView.mTvMessage.isClickable = true
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvMessage,R.mipmap.ic_dispute_question_mark)

        } else {
            itemView.mTvMessage.isClickable = false
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvMessage,0)
//            itemView.mTvMessage.text = data?.message
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}