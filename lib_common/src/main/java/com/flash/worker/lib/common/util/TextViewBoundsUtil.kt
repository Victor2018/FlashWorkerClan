package com.flash.worker.lib.common.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TextViewBoundsUtil
 * Author: Victor
 * Date: 2020/12/10 12:30
 * Description: 
 * -----------------------------------------------------------------
 */
object TextViewBoundsUtil {
    fun setTvDrawableLeft(
        context: Context?,
        textView: TextView?,
        icResId: Int
    ) {
        var drawable: Drawable? = null
        if (icResId != 0) {
            drawable = context?.resources?.getDrawable(icResId)
        }
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

        var drawables = textView?.compoundDrawables
        if (drawables != null && drawables.size >= 4) {
            drawables[0] = drawable
            textView?.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3])
        } else {
            textView?.setCompoundDrawables(drawable, null, null, null)
        }
    }

    fun setTvDrawableRight(
        context: Context?,
        textView: TextView?,
        icResId: Int
    ) {
        var drawable: Drawable? = null
        if (icResId != 0) {
            drawable = context?.resources?.getDrawable(icResId)
        }
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)


        var drawables = textView?.compoundDrawables
        if (drawables != null && drawables.size >= 4) {
            drawables[2] = drawable
            textView?.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3])
        } else {
            textView?.setCompoundDrawables(null,null,drawable,null)
        }
    }

    fun setTvDrawableTop(
        context: Context?,
        textView: TextView?,
        icResId: Int
    ) {
        var drawable: Drawable? = null
        if (icResId != 0) {
            drawable = context?.resources?.getDrawable(icResId)
        }
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

        var drawables = textView?.compoundDrawables
        if (drawables != null && drawables.size >= 4) {
            drawables[1] = drawable
            textView?.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3])
        } else {
            textView?.setCompoundDrawables(null, drawable, null, null)
        }
    }

    fun setTvDrawableBottom(
        context: Context?,
        textView: TextView?,
        icResId: Int
    ) {
        var drawable: Drawable? = null
        if (icResId != 0) {
            drawable = context?.resources?.getDrawable(icResId)
        }
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

        var drawables = textView?.compoundDrawables
        if (drawables != null && drawables.size >= 4) {
            drawables[3] = drawable
            textView?.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3])
        } else {
            textView?.setCompoundDrawables(null, null, null, drawable)
        }
    }

}