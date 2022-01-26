package com.flash.worker.lib.common.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.flash.worker.lib.common.app.App


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: KeyBoardUtil
 * Author: Victor
 * Date: 2020/12/18 9:24
 * Description: 
 * -----------------------------------------------------------------
 */
object KeyBoardUtil {
    /**
     * 显示系统软键盘
     */
    fun showKeyBoard(context: Context?, view: View?) {
        view?.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 隐藏系统键盘
     * @param view
     */
    fun hideKeyBoard(context: Context?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun hideKeyBoard(activity: Activity) {
        val window = activity.window
        var view: View? = window.currentFocus
        if (view == null) {
            val decorView: View = window.decorView
            val focusView = decorView.findViewWithTag<View>("keyboardTagView")
            if (focusView == null) {
                view = EditText(window.context)
                view!!.tag = "keyboardTagView"
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view!!.requestFocus()
        }
    }

    /**
     * Toggle the soft input display or not.
     */
    fun toggleSoftInput() {
        val imm = App.get().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
    }

}