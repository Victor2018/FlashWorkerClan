package com.flash.worker.lib.common.view.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MaxHeightNestedScrollView
 * Author: Victor
 * Date: 2021/9/26 10:03
 * Description: 
 * -----------------------------------------------------------------
 */
class MaxHeightNestedScrollView: NestedScrollView {
    private var maxHeight = -1

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context,attrs,defStyleAttr)
    }

    // Modified changes
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MaxHeightNestedScrollView, defStyleAttr, 0)
        maxHeight = a.getDimensionPixelSize(R.styleable.MaxHeightNestedScrollView_maxHeight, 0)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}