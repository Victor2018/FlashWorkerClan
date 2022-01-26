package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnDeleteMyEvaluationListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DeleteMyEvaluationPopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class DeleteMyEvaluationPopWindow(context: Context?): AbsPopWindow(context), View.OnClickListener {

    var mOnDeleteMyEvaluationListener: OnDeleteMyEvaluationListener? = null

    override fun bindContentView() = R.layout.pop_delete_my_evaluation

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.mTvDelete)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDelete -> {
                mOnDeleteMyEvaluationListener?.OnDeleteMyEvaluation()
                dismiss()
            }
        }
    }

}