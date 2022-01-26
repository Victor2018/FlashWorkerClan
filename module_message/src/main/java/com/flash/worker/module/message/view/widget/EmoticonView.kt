package com.flash.worker.module.message.view.widget

import android.R.attr.bottom
import android.R.attr.top
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.StickerCategory
import com.flash.worker.module.message.data.StickerItem
import com.flash.worker.module.message.interfaces.IEmoticonCategoryChanged
import com.flash.worker.module.message.interfaces.IEmoticonSelectedListener
import com.flash.worker.module.message.util.EmojiManager
import com.flash.worker.module.message.util.MoonUtil
import com.flash.worker.module.message.util.StickerManager
import com.flash.worker.module.message.view.adapter.EmojiAdapter
import com.flash.worker.module.message.view.adapter.StickerAdapter
import java.util.*
import kotlin.collections.HashMap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmoticonView
 * Author: Victor
 * Date: 2021/4/27 14:23
 * Description: 
 * -----------------------------------------------------------------
 */
class EmoticonView(context: Context, mlistener: IEmoticonSelectedListener?,
                   mCurPage: ViewPager?, pageNumberLayout: LinearLayout?) {
    private var emotPager: ViewPager? = null
    private var pageNumberLayout: LinearLayout? = null
    private var messageEditText: EditText? = null

    fun attachEditText(messageEditText: EditText?) {
        this.messageEditText = messageEditText
    }

    /**
     * 总页数.
     */
    private var pageCount = 0

    companion object {
        /**
         * 每页显示的数量，Adapter保持一致.
         */
        val EMOJI_PER_PAGE: Int = 27 // 最后一个是删除键

        val STICKER_PER_PAGE: Int = 8
    }


    private var context: Context? = null
    private var listener: IEmoticonSelectedListener? = null
    private val pagerAdapter = EmoticonViewPaperAdapter()

    /**
     * 所有表情贴图支持横向滑动切换
     */
    private var categoryIndex // 当套贴图的在picker中的索引
            = 0
    private var isDataInitialized = false // 数据源只需要初始化一次,变更时再初始化

    private var categoryDataList // 表情贴图数据源
            : HashMap<Int,StickerCategory?>? = null
    private var categoryPageNumberList // 每套表情贴图对应的页数
            : MutableList<Int>? = null
    private val pagerIndexInfo = IntArray(2) // 0：category index；1：pager index in category

    private var categoryChangedCallback: IEmoticonCategoryChanged? = null// 横向滑动切换时回调picker

    init {
        this.context = context.applicationContext
        listener = mlistener
        this.pageNumberLayout = pageNumberLayout
        emotPager = mCurPage
        emotPager?.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (categoryDataList != null) {
                    // 显示所有贴图表情
                    setCurStickerPage(position)
                    if (categoryChangedCallback != null) {
                        val currentCategoryChecked = pagerIndexInfo[0] // 当前那种类别被选中
                        categoryChangedCallback?.onCategoryChanged(currentCategoryChecked)
                    }
                } else {
                    // 只显示表情
                    setCurEmotionPage(position)
                }
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        emotPager?.adapter = pagerAdapter
        emotPager?.offscreenPageLimit = 1
    }

    fun setCategoryDataReloadFlag() {
        isDataInitialized = false
    }

    fun showStickers(index: Int) {
        // 判断是否需要变化
        if (isDataInitialized && getPagerInfo(emotPager!!.currentItem) != null
            && pagerIndexInfo[0] == index && pagerIndexInfo[1] == 0
        ) {
            return
        }
        categoryIndex = index
        showStickerGridView()
    }

    fun showEmojis() {
        showEmojiGridView()
    }

    private fun getCategoryPageCount(category: StickerCategory?): Int {
        return if (category == null) {
            Math.ceil(EmojiManager.getDisplayCount() / EMOJI_PER_PAGE.toDouble()).toInt()
        } else {
            if (category.hasStickers()) {
                val stickers: List<StickerItem> = category.getStickers() as List<StickerItem>
                Math.ceil(
                    (stickers.size / STICKER_PER_PAGE).toDouble()
                ).toInt()
            } else {
                1
            }
        }
    }

    private fun setCurPage(page: Int, pageCount: Int) {
        val hasCount = pageNumberLayout?.childCount ?: 0
        val forMax = Math.max(hasCount, pageCount)
        var imgCur: ImageView? = null
        for (i in 0 until forMax) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    pageNumberLayout?.getChildAt(i)?.visibility = View.GONE
                    continue
                } else {
                    imgCur = pageNumberLayout?.getChildAt(i) as ImageView?
                }
            } else {
                if (i < hasCount) {
                    imgCur = pageNumberLayout?.getChildAt(i) as ImageView?
                } else {
                    imgCur = ImageView(context)
                    imgCur.setBackgroundResource(R.drawable.nim_view_pager_indicator_selector)

                    val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(DensityUtil.dip2px(context!!,2f), 0,
                        DensityUtil.dip2px(context!!,2f), 0)
                    imgCur.layoutParams = lp

                    pageNumberLayout?.addView(imgCur)
                }
            }
            imgCur?.id = i
            imgCur?.isSelected = i == page // 判断当前页码来更新
            imgCur?.visibility = View.VISIBLE
        }
    }

    /**
     * ******************************** 表情  *******************************
     */
    private fun showEmojiGridView() {
        pageCount =
            Math.ceil(EmojiManager.getDisplayCount() / EMOJI_PER_PAGE.toDouble()).toInt()
        pagerAdapter.notifyDataSetChanged()
        resetEmotionPager()
    }

    private fun resetEmotionPager() {
        setCurEmotionPage(0)
        emotPager!!.setCurrentItem(0, false)
    }

    private fun setCurEmotionPage(position: Int) {
        setCurPage(position, pageCount)
    }

    var emojiListener =
        AdapterView.OnItemClickListener { arg0, arg1, arg2, arg3 ->
            val position = emotPager?.currentItem ?: 0
            var pos = position // 如果只有表情，那么用默认方式计算
            if (categoryDataList != null && categoryPageNumberList != null) {
                // 包含贴图
                getPagerInfo(position)
                pos = pagerIndexInfo[1]
            }
            val index = arg2 + pos * EMOJI_PER_PAGE
            if (listener != null) {
                val count: Int = EmojiManager.getDisplayCount()
                if (arg2 == EMOJI_PER_PAGE || index >= count) {
                    listener?.onEmojiSelected("/DEL")
                    onEmojiSelected("/DEL")
                } else {
                    val text: String? = EmojiManager.getDisplayText(arg3.toInt())
                    if (!TextUtils.isEmpty(text)) {
                        listener?.onEmojiSelected(text)
                        onEmojiSelected(text!!)
                    }
                }
            }
        }

    /**
     * 若EditText交给EmotionView保管（调用attachEditText方法），则由EmotionView处理表情图标
     *
     * @param key
     */
    private fun onEmojiSelected(key: String) {
        if (messageEditText == null) {
            return
        }
        val mEditable = messageEditText!!.text
        if (key == "/DEL") {
            messageEditText!!.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL
                )
            )
        } else {
            var start = messageEditText!!.selectionStart
            var end = messageEditText!!.selectionEnd
            start = if (start < 0) 0 else start
            end = if (start < 0) 0 else end
            mEditable.replace(start, end, key)
            val editEnd = messageEditText!!.selectionEnd
            MoonUtil.replaceEmoticons(
                App.get(),
                mEditable,
                0,
                mEditable.toString().length
            )
            messageEditText!!.setSelection(editEnd)
        }
    }

    /**
     * ******************************** 贴图  *******************************
     */
    private fun showStickerGridView() {
        initData()
        pagerAdapter.notifyDataSetChanged()

        // 计算起始的pager index
        var position = 0
        for (i in categoryPageNumberList!!.indices) {
            if (i == categoryIndex) {
                break
            }
            position += categoryPageNumberList!![i]
        }
        setCurStickerPage(position)
        emotPager!!.setCurrentItem(position, false)
    }

    private fun initData() {
        if (isDataInitialized) { //数据已经初始化，未变动不重新加载数据
            return
        }
        if (categoryDataList == null) {
            categoryDataList = HashMap()
        }
        if (categoryPageNumberList == null) {
            categoryPageNumberList = ArrayList()
        }
        categoryDataList!!.clear()
        categoryPageNumberList!!.clear()
        val manager: StickerManager = StickerManager.getInstance()

        var emojiCategory = StickerCategory("emoji","emoji",false,0)
        categoryDataList?.put(0,null) // 表情
        categoryPageNumberList!!.add(getCategoryPageCount(null))
        val categories: List<StickerCategory> = manager.getCategories()!!
//        categoryDataList!!.addAll(categories) // 贴图
        for (i in 0 until categories.size) {
            categoryDataList?.put(i+1,categories[i])
        }
        for (c in categories) {
            categoryPageNumberList!!.add(getCategoryPageCount(c))
        }
        pageCount = 0 //总页数
        for (count in categoryPageNumberList!!) {
            pageCount += count
        }
        isDataInitialized = true
    }

    // 给定pager中的索引，返回categoryIndex和positionInCategory
    private fun getPagerInfo(position: Int): IntArray? {
        if (categoryDataList == null || categoryPageNumberList == null) {
            return pagerIndexInfo
        }
        var cIndex = categoryIndex
        var startIndex = 0
        var pageNumberPerCategory = 0
        for (i in categoryPageNumberList!!.indices) {
            pageNumberPerCategory = categoryPageNumberList!![i]
            if (position < startIndex + pageNumberPerCategory) {
                cIndex = i
                break
            }
            startIndex += pageNumberPerCategory
        }
        pagerIndexInfo[0] = cIndex
        pagerIndexInfo[1] = position - startIndex
        return pagerIndexInfo
    }

    private fun setCurStickerPage(position: Int) {
        getPagerInfo(position)
        val categoryIndex = pagerIndexInfo[0]
        val pageIndexInCategory = pagerIndexInfo[1]
        val categoryPageCount = categoryPageNumberList!![categoryIndex]
        setCurPage(pageIndexInCategory, categoryPageCount)
    }

    fun setCategoryChangCheckedCallback(callback: IEmoticonCategoryChanged?) {
        categoryChangedCallback = callback
    }

    private val stickerListener =
        AdapterView.OnItemClickListener { arg0, arg1, arg2, arg3 ->
            val position = emotPager!!.currentItem
            getPagerInfo(position)
            val cIndex = pagerIndexInfo[0]
            val pos = pagerIndexInfo[1]
            val c: StickerCategory? = categoryDataList!![cIndex]
            val index = arg2 + pos * STICKER_PER_PAGE // 在category中贴图的index
            if (index >= c?.getStickers()?.size!!) {
                Log.i(
                    "sticker",
                    "index " + index + " larger than size " + c?.getStickers()?.size
                )
                return@OnItemClickListener
            }
            if (listener != null) {
                val manager: StickerManager = StickerManager.getInstance()
                val stickers: List<StickerItem> = c?.getStickers()!!
                val sticker: StickerItem = stickers[index]
                val real: StickerCategory =
                    manager.getCategory(sticker.getCategory()) ?: return@OnItemClickListener
                listener?.onStickerSelected(sticker.getCategory(), sticker.getName())
            }
        }


    /**
     * ***************************** PagerAdapter ****************************
     */
    private inner class EmoticonViewPaperAdapter : PagerAdapter() {
        override fun isViewFromObject(
            view: View,
            `object`: Any
        ): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return if (pageCount == 0) 1 else pageCount
        }

        override fun instantiateItem(
            container: ViewGroup,
            position: Int
        ): Any {
            var category: StickerCategory?
            val pos: Int
            if (categoryDataList != null && categoryDataList?.size!! > 0
                && categoryPageNumberList != null && categoryPageNumberList?.size!! > 0
            ) {
                // 显示所有贴图&表情
                getPagerInfo(position)
                val cIndex: Int = pagerIndexInfo.get(0)
                category = categoryDataList?.get(cIndex)
                pos = pagerIndexInfo.get(1)
            } else {
                // 只显示表情
                category = null
                pos = position
            }
            return if (category == null) {
                val rl = RelativeLayout(context)
                pageNumberLayout?.setVisibility(View.VISIBLE)
                val gridView = GridView(context)
                gridView.onItemClickListener = emojiListener
                gridView.adapter = EmojiAdapter(context, pos * EMOJI_PER_PAGE)
                gridView.numColumns = 7
                gridView.horizontalSpacing = 5
                gridView.verticalSpacing = 5
                gridView.gravity = Gravity.CENTER
                gridView.setSelector(R.drawable.nim_emoji_item_selector)
                rl.gravity = Gravity.CENTER
                rl.addView(gridView)
                container.addView(rl)
                rl
            } else {
                pageNumberLayout?.setVisibility(View.VISIBLE)
                val gridView = GridView(context)
                gridView.setPadding(10, 0, 10, 0)
                gridView.onItemClickListener = stickerListener
                gridView.adapter = StickerAdapter(context, category, pos * STICKER_PER_PAGE)
                gridView.numColumns = 4
                gridView.horizontalSpacing = 5
                gridView.gravity = Gravity.CENTER
                gridView.setSelector(R.drawable.nim_emoji_item_selector)
                container.addView(gridView)
                gridView
            }
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            val layout = `object` as View
            container.removeView(layout)
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
}