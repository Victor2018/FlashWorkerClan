package com.flash.worker.lib.common.view.adapter.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AlphaAnimatorAdapter.java
 * Author: Victor
 * Date: 2019/11/11 16:38
 * Description:
 * -----------------------------------------------------------------
 */
class AlphaAnimatorAdapter <T: RecyclerView.ViewHolder>
(var adapter: RecyclerView.Adapter<T>?, var recyclerView: RecyclerView): AnimatorAdapter<T>(adapter,recyclerView) {
    private val ALPHA = "alpha"

    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, ALPHA, 0f, 1f))
    }

}