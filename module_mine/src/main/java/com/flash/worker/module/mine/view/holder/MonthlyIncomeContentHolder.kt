package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildMemberInfo
import com.flash.worker.lib.coremodel.data.bean.MemberIncomeRankInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_monthly_income_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MonthlyIncomeContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class MonthlyIncomeContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: MemberIncomeRankInfo?) {
        if (adapterPosition == 0) {
            itemView.mTvRanking.text = ""
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvRanking,R.mipmap.ic_monthly_income_1)
        } else if (adapterPosition == 1) {
            itemView.mTvRanking.text = ""
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvRanking,R.mipmap.ic_monthly_income_2)
        } else if (adapterPosition == 2) {
            itemView.mTvRanking.text = ""
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvRanking,R.mipmap.ic_monthly_income_3)
        } else {
            itemView.mTvRanking.text = (adapterPosition + 1).toString()
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvRanking,0)
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)

        if (data?.sex == 0) {
            itemView.mIvSex.setImageResource(R.mipmap.ic_mine_female)
        } else if (data?.sex == 1) {
            itemView.mIvSex.setImageResource(R.mipmap.ic_mine_male)
        } else {
            itemView.mIvSex.setImageResource(0)
        }

        itemView.mTvUserName.text = data?.username
        itemView.mTvMonthIncome.text = AmountUtil.addCommaDots(data?.incomeAmount)

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}