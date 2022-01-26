package com.flash.worker.lib.common.view.widget

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ResUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireDropDownMenu
 * Author: Victor
 * Date: 2021/1/5 11:15
 * Description: 
 * -----------------------------------------------------------------
 */
class DropDownMenu: LinearLayout {
    val TAG = "DropDownMenu"
    //顶部菜单布局
    var tabMenuView: LinearLayout? = null

    //底部容器，包含popupMenuViews，maskView
    var containerView: FrameLayout? = null

    //弹出菜单父布局
    var popupMenuViews: FrameLayout? = null

    //遮罩半透明View，点击可关闭DropDownMenu
    private var maskView: View? = null

    //tabMenuView里面选中的tab位置，-1表示未选中
    private var current_tab_position = -1

    //分割线颜色
    private var dividerColor = -0x333334

    //tab选中颜色
    private var textSelectedColor = -0x76f37b

    //tab未选中颜色
    private var textUnselectedColor = -0xeeeeef

    //遮罩颜色
    private var maskColor = -0x77777778

    //tab字体大小
    private var menuTextSize = 14
    //tab字体是否加粗
    private var ddIsFakeBoldText = false

    //menu左右边距
    private var tabPadingLeft = 0
    private var tabPadingRight = 0

    //tab选中图标
    private var menuSelectedIcon = 0

    //tab未选中图标
    private var menuUnselectedIcon = 0

    private var menuHeighPercent = 0.5f

    var mOnDropDownMenuClickListener: OnDropDownMenuClickListener? = null

    var isFillWidth: Boolean = true
    var isTextViewFillWidth: Boolean = false
    var textViewGravity: Int = Gravity.CENTER

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = VERTICAL

        //为DropDownMenu添加自定义属性

        //为DropDownMenu添加自定义属性
        var menuBackgroundColor = -0x1
        var underlineColor = -0x333334
        val a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu)
        underlineColor = a.getColor(R.styleable.DropDownMenu_ddunderlineColor, underlineColor)
        dividerColor = a.getColor(R.styleable.DropDownMenu_dddividerColor, dividerColor)
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor)
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor)
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor)
        maskColor = a.getColor(R.styleable.DropDownMenu_ddmaskColor, maskColor)
        ddIsFakeBoldText = a.getBoolean(R.styleable.DropDownMenu_ddIsFakeBoldText, ddIsFakeBoldText)
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize)
        tabPadingLeft = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTabLeft, tabPadingLeft)
        tabPadingRight = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTabRight, tabPadingRight)
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon)
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon)
        menuHeighPercent = a.getFloat(R.styleable.DropDownMenu_ddmenuMenuHeightPercent, menuHeighPercent)
        a.recycle()

        //初始化tabMenuView并添加到tabMenuView

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = LinearLayout(context)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tabMenuView!!.orientation = HORIZONTAL
        tabMenuView!!.setBackgroundColor(menuBackgroundColor)
        tabMenuView!!.layoutParams = params
        addView(tabMenuView, 0)

        //为tabMenuView添加下划线
        val underLine = View(getContext())
        underLine.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1.0f))
        underLine.setBackgroundColor(underlineColor)
        addView(underLine, 1)

        //初始化containerView并将其添加到DropDownMenu
        containerView = FrameLayout(context)
        containerView!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(containerView, 2)
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    fun setDropDownMenu(tabTexts: List<String>, popupViews: List<View>, contentView: View?) {
        require(tabTexts.size == popupViews.size) { "params not match, tabTexts.size() should be equal popupViews.size()" }
        for (i in tabTexts.indices) {
            addTab(tabTexts, i)
        }
        containerView?.addView(contentView, 0)
        maskView = View(context)
        maskView?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        maskView?.setBackgroundColor(maskColor)
        maskView?.setOnClickListener { closeMenu() }
        containerView?.addView(maskView, 1)
        maskView!!.visibility = View.GONE
        if (containerView?.getChildAt(2) != null) {
            containerView?.removeViewAt(2)
        }
        popupMenuViews = FrameLayout(context)

        if (menuHeighPercent > 0) {
            popupMenuViews!!.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (
            DensityUtil.getDisplayHeight() * menuHeighPercent).toInt())
        } else {
            contentView?.post {
                popupMenuViews?.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        contentView?.height ?: 0)
            }
        }

        popupMenuViews?.visibility = View.GONE
        containerView?.addView(popupMenuViews, 2)
        for (i in popupViews.indices) {
            popupViews[i].layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            popupMenuViews!!.addView(popupViews[i], i)
        }
    }

    private fun addTab(tabTexts: List<String>, i: Int) {

        val tabLayout = LinearLayout(context)
        tabLayout.gravity = Gravity.CENTER
        tabLayout.orientation = HORIZONTAL

        var tabParams = LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

        if (!isFillWidth) {
            tabParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        tabParams.leftMargin = tabPadingLeft
        tabParams.rightMargin = tabPadingRight
        tabLayout.layoutParams = tabParams

        val tab = TextView(context)
        tab.setSingleLine()
        tab.ellipsize = TextUtils.TruncateAt.END
        tab.gravity = textViewGravity
        if (ddIsFakeBoldText) {
            tab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
        }
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize.toFloat())
        tab.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (isTextViewFillWidth) {
            tab.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        tab.setTextColor(textUnselectedColor)
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(menuUnselectedIcon), null)
        tab.compoundDrawablePadding = ResUtils.getDimenFloatPixRes(R.dimen.dp_13).toInt()
        tab.text = tabTexts[i]
        tab.setPadding(dpTpPx(5f), dpTpPx(12f), dpTpPx(5f), dpTpPx(12f))
        //添加点击事件
        tab.setOnClickListener { switchMenu(tabLayout) }

        tabLayout.addView(tab)
        tabMenuView!!.addView(tabLayout)

        //添加分割线
        if (i < tabTexts.size - 1) {
            val view = View(context)
            view.layoutParams = LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT)
            view.setBackgroundColor(dividerColor)
            tabMenuView!!.addView(view)
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    fun setTabText(text: String?) {
        if (current_tab_position != -1) {
            ((tabMenuView!!.getChildAt(current_tab_position) as LinearLayout).getChildAt(0) as TextView).text = text
        }
    }

    fun setTabClickable(clickable: Boolean) {
        var i = 0
        while (i < tabMenuView!!.childCount) {
            tabMenuView!!.getChildAt(i).isClickable = clickable
            i = i + 2
        }
    }

    /**
     * 关闭菜单
     */
    fun closeMenu() {
        try {
            if (current_tab_position != -1) {
                ((tabMenuView!!.getChildAt(current_tab_position) as LinearLayout).getChildAt(0) as TextView).setTextColor(textUnselectedColor)
                ((tabMenuView!!.getChildAt(current_tab_position) as LinearLayout).getChildAt(0) as TextView).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        resources.getDrawable(menuUnselectedIcon), null)
                popupMenuViews!!.visibility = View.GONE
                popupMenuViews!!.animation = AnimationUtils.loadAnimation(context, R.anim.dd_menu_out)
                maskView!!.visibility = View.GONE
                maskView!!.animation = AnimationUtils.loadAnimation(context, R.anim.dd_mask_out)
                current_tab_position = -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    fun isShowing(): Boolean {
        return current_tab_position != -1
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private fun switchMenu(target: View) {
        var i = 0
        while (i < tabMenuView!!.childCount) {
            if (target === tabMenuView!!.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu()
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews!!.visibility = View.VISIBLE
                        popupMenuViews!!.animation = AnimationUtils.loadAnimation(context, R.anim.dd_menu_in)
                        maskView!!.visibility = View.VISIBLE
                        maskView!!.animation = AnimationUtils.loadAnimation(context, R.anim.dd_mask_in)
                        popupMenuViews!!.getChildAt(i / 2).visibility = View.VISIBLE
                    } else {
                        popupMenuViews!!.getChildAt(i / 2).visibility = View.VISIBLE
                    }
                    current_tab_position = i
                    ((tabMenuView!!.getChildAt(i) as LinearLayout).getChildAt(0) as TextView).setTextColor(textSelectedColor)
                    ((tabMenuView!!.getChildAt(i) as LinearLayout).getChildAt(0) as TextView).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            resources.getDrawable(menuSelectedIcon), null)
                }
            } else {
                ((tabMenuView!!.getChildAt(i) as LinearLayout).getChildAt(0) as TextView).setTextColor(textUnselectedColor)
                ((tabMenuView!!.getChildAt(i) as LinearLayout).getChildAt(0) as TextView).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        resources.getDrawable(menuUnselectedIcon), null)
                popupMenuViews!!.getChildAt(i / 2).visibility = View.GONE
            }
            i = i + 2
        }
        var position = current_tab_position / 2
        Loger.e(TAG, "switchMenu-position = " + position)
        mOnDropDownMenuClickListener?.OnDropDownMenuClick(position)
    }

    fun dpTpPx(value: Float): Int {
        val dm = resources.displayMetrics
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5).toInt()
    }
}