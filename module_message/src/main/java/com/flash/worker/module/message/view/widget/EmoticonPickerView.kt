package com.flash.worker.module.message.view.widget

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.StickerCategory
import com.flash.worker.module.message.interfaces.IEmoticonCategoryChanged
import com.flash.worker.module.message.interfaces.IEmoticonSelectedListener
import com.flash.worker.module.message.util.BitmapDecoder
import com.flash.worker.module.message.util.StickerManager
import java.io.IOException
import java.io.InputStream


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmoticonPickerView
 * Author: Victor
 * Date: 2021/4/27 14:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmoticonPickerView: LinearLayout, IEmoticonCategoryChanged {
    private var mContext: Context? = null
    private var listener: IEmoticonSelectedListener? = null

    private var loaded = false

    var withSticker = false

    private var gifView: EmoticonView? = null

    private var currentEmojiPage: ViewPager? = null

    private var pageNumberLayout: LinearLayout? = null//页面布局

    private var scrollView: HorizontalScrollView? = null

    private var tabView: LinearLayout? = null

    private var categoryIndex = 0

    private var uiHandler: Handler? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        uiHandler = Handler(context.mainLooper)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.nim_emoji_layout, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupEmojView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun setListener(listener: IEmoticonSelectedListener?) {
        if (listener != null) {
            this.listener = listener
        } else {
            Log.i("sticker", "listener is null")
        }
    }

    protected fun setupEmojView() {
        currentEmojiPage = findViewById<View>(R.id.scrPlugin) as ViewPager
        pageNumberLayout = findViewById<View>(R.id.layout_scr_bottom) as LinearLayout
        tabView = findViewById<View>(R.id.emoj_tab_view) as LinearLayout
        scrollView = findViewById<View>(R.id.emoj_tab_view_container) as HorizontalScrollView
        findViewById<View>(R.id.top_divider_line).visibility = View.VISIBLE
    }

    // 添加各个tab按钮
    var tabCheckListener =
        OnClickListener { v -> onEmoticonBtnChecked(v.id) }

    private fun loadStickers() {
        if (!withSticker) {
            scrollView?.visibility = View.GONE
            return
        }
        val manager: StickerManager = StickerManager.getInstance()
        tabView?.removeAllViews()
        var index = 0

        // emoji表情
        var btn = addEmoticonTabBtn(index++, tabCheckListener)
        btn.setNormalImageId(R.mipmap.nim_emoji_icon_inactive)
        btn.setCheckedImageId(R.mipmap.nim_emoji_icon)

        // 贴图
        val categories = manager.getCategories()
        categories?.forEach {
            btn = addEmoticonTabBtn(index++, tabCheckListener)
            setCheckedButtomImage(btn, it)
        }
    }


    private fun addEmoticonTabBtn(
        index: Int,
        listener: OnClickListener
    ): CheckedImageButton {
        val emotBtn = CheckedImageButton(context)
        emotBtn.setNormalBkResId(R.drawable.nim_sticker_button_background_normal_layer_list)
        emotBtn.setCheckedBkResId(R.drawable.nim_sticker_button_background_pressed_layer_list)
        emotBtn.id = index
        emotBtn.setOnClickListener(listener)
        emotBtn.scaleType = ImageView.ScaleType.FIT_CENTER
        emotBtn.setPaddingValue(DensityUtil.dip2px(mContext!!,7f))
        val emojiBtnWidth: Int = DensityUtil.dip2px(mContext!!,50f)
        val emojiBtnHeight: Int = DensityUtil.dip2px(mContext!!,44f)
        tabView?.addView(emotBtn)
        val emojBtnLayoutParams = emotBtn.layoutParams
        emojBtnLayoutParams.width = emojiBtnWidth
        emojBtnLayoutParams.height = emojiBtnHeight
        emotBtn.layoutParams = emojBtnLayoutParams
        return emotBtn
    }

    private fun setCheckedButtomImage(
        btn: CheckedImageButton,
        category: StickerCategory
    ) {
        try {
            var `is`: InputStream = category.getCoverNormalInputStream(context)!!
            if (`is` != null) {
                val bmp: Bitmap = BitmapDecoder.decode(`is`)!!
                btn.setNormalImage(bmp)
                `is`.close()
            }
            `is` = category.getCoverPressedInputStream(context)!!
            if (`is` != null) {
                val bmp: Bitmap = BitmapDecoder.decode(`is`)!!
                btn.setCheckedImage(bmp)
                `is`.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun onEmoticonBtnChecked(index: Int) {
        updateTabButton(index)
        showEmotPager(index)
    }

    private fun updateTabButton(index: Int) {
        var childCount = tabView?.childCount ?: 0
        for (i in 0 until childCount) {
            var child = tabView?.getChildAt(i)
            if (child is FrameLayout) {
                child = child.getChildAt(0)
            }
            if (child != null && child is CheckedImageButton) {
                val tabButton = child
                if (tabButton.isChecked() && i != index) {
                    tabButton.setChecked(false)
                } else if (!tabButton.isChecked() && i == index) {
                    tabButton.setChecked(true)
                }
            }
        }
    }

    private fun showEmotPager(index: Int) {
        if (gifView == null) {
            gifView = EmoticonView(mContext!!, listener, currentEmojiPage, pageNumberLayout)
            gifView?.setCategoryChangCheckedCallback(this)
        }
        gifView?.showStickers(index)
    }

    private fun showEmojiView() {
        if (gifView == null) {
            gifView = EmoticonView(context, listener, currentEmojiPage, pageNumberLayout)
        }
        gifView?.showEmojis()
    }

    private fun show() {
        if (listener == null) {
            Log.i("sticker", "show picker view when listener is null")
        }
        if (!withSticker) {
            showEmojiView()
        } else {
            onEmoticonBtnChecked(0)
            setSelectedVisible(0)
        }
    }

    private fun setSelectedVisible(index: Int) {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (scrollView!!.getChildAt(0).width == 0) {
                    uiHandler!!.postDelayed(this, 100)
                }
                var x = -1
                val child = tabView!!.getChildAt(index)
                if (child != null) {
                    if (child.right > scrollView!!.width) {
                        x = child.right - scrollView!!.width
                    }
                }
                if (x != -1) {
                    scrollView!!.smoothScrollTo(x, 0)
                }
            }
        }
        uiHandler!!.postDelayed(runnable, 100)
    }


    override fun onCategoryChanged(index: Int) {
        if (categoryIndex == index) {
            return
        }
        categoryIndex = index
        updateTabButton(index)
    }

    /**
     * 显示表情视图并设置点击监听（监听表情、贴图的点击事件）
     *
     * @param listener
     */
    fun show(listener: IEmoticonSelectedListener?) {
        setListener(listener)
        if (loaded) return
        loadStickers()
        loaded = true
        show()
    }

    /**
     * 将EditText交给EmotionView保管，点击表情时自动插入表情
     *
     * @param messageEditText
     */
    fun attachEditText(messageEditText: EditText?) {
        if (gifView != null) gifView?.attachEditText(messageEditText)
    }

}