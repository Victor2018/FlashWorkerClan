package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.flash.worker.lib.common.R
import kotlinx.coroutines.Runnable
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WheelView
 * Author: Victor
 * Date: 2021/5/6 14:29
 * Description: 
 * -----------------------------------------------------------------
 */
class WheelView: View,Runnable {

    private val TAG = "WheelView"

    private val DEFAULT_LINE_SPACING: Float = dp2px(2f)
    private val DEFAULT_TEXT_SIZE: Float = sp2px(15f)
    private val DEFAULT_TEXT_BOUNDARY_MARGIN: Float = dp2px(2f)
    private val DEFAULT_DIVIDER_HEIGHT: Float = dp2px(1f)
    private val DEFAULT_NORMAL_TEXT_COLOR = Color.DKGRAY
    private val DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK
    private val DEFAULT_VISIBLE_ITEM = 5
    private val DEFAULT_SCROLL_DURATION = 250
    private val DEFAULT_CLICK_CONFIRM: Long = 120
    private val DEFAULT_INTEGER_FORMAT = "%02d"

    //默认折射比值，通过字体大小来实现折射视觉差
    private val DEFAULT_REFRACT_RATIO = 1f

    companion object {
        //文字对齐方式
        const val TEXT_ALIGN_LEFT = 0
        const val TEXT_ALIGN_CENTER = 1
        const val TEXT_ALIGN_RIGHT = 2

        //滚动状态
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SCROLLING = 2

        //弯曲效果对齐方式
        const val CURVED_ARC_DIRECTION_LEFT = 0
        const val CURVED_ARC_DIRECTION_CENTER = 1
        const val CURVED_ARC_DIRECTION_RIGHT = 2

        //分割线填充类型
        const val DIVIDER_TYPE_FILL = 0
        const val DIVIDER_TYPE_WRAP = 1
    }



    val DEFAULT_CURVED_FACTOR = 0.75f

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    //字体大小
    private var mTextSize = 0f

    //是否自动调整字体大小以显示完全
    private var isAutoFitTextSize = false
    private var mFontMetrics: Paint.FontMetrics? = null

    //每个item的高度
    private var mItemHeight = 0

    //文字的最大宽度
    private var mMaxTextWidth = 0

    //文字中心距离baseline的距离
    private var mCenterToBaselineY = 0

    //可见的item条数
    private var mVisibleItems = 0

    //每个item之间的空间，行间距
    private var mLineSpacing = 0f

    //是否循环滚动
    private var isCyclic = false

    //文字对齐方式
    private var mTextAlign = 0

    //文字颜色
    private var mTextColor = 0

    //选中item文字颜色
    private var mSelectedItemTextColor = 0

    //是否显示分割线
    private var isShowDivider = false

    //分割线的颜色
    private var mDividerColor = 0

    //分割线高度
    private var mDividerSize = 0f

    //分割线填充类型
    private var mDividerType = 0

    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    private var mDividerPaddingForWrap = 0f

    //分割线两端形状，默认圆头
    private var mDividerCap = Paint.Cap.ROUND

    //分割线和选中区域偏移，实现扩大选中区域
    private var mDividerOffset = 0f

    //是否绘制选中区域
    private var isDrawSelectedRect = false

    //选中区域颜色
    private var mSelectedRectColor = 0

    //文字起始X
    private var mStartX = 0

    //X轴中心点
    private var mCenterX = 0

    //Y轴中心点
    private var mCenterY = 0

    //选中边界的上下限制
    private var mSelectedItemTopLimit = 0
    private var mSelectedItemBottomLimit = 0

    //裁剪边界
    private var mClipLeft = 0
    private var mClipTop = 0
    private var mClipRight = 0
    private var mClipBottom = 0

    //绘制区域
    private var mDrawRect: Rect? = null

    //字体外边距，目的是留有边距
    private var mTextBoundaryMargin = 0f

    //数据为Integer类型时，是否需要格式转换
    private var isIntegerNeedFormat = false

    //数据为Integer类型时，转换格式，默认转换为两位数
    private var mIntegerFormat: String? = null

    //3D效果
    private var mCamera: Camera? = null
    private var mMatrix: Matrix? = null

    //是否是弯曲（3D）效果
    private var isCurved = false

    //弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    private var mCurvedArcDirection = 0

    //弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    private var mCurvedArcDirectionFactor = 0f

    //选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    //(普通效果和3d效果都适用)
    private var mRefractRatio = 0f

    //数据列表
    private var mDataList: List<String> = ArrayList(1)

    //数据变化时，是否重置选中下标到第一个位置
    private var isResetSelectedPosition = false

    private var mVelocityTracker: VelocityTracker? = null
    private var mMaxFlingVelocity = 0
    private var mMinFlingVelocity = 0
    private var mScroller: Scroller? = null

    //最小滚动距离，上边界
    private var mMinScrollY = 0

    //最大滚动距离，下边界
    private var mMaxScrollY = 0

    //Y轴滚动偏移
    private var mScrollOffsetY = 0

    //Y轴已滚动偏移，控制重绘次数
    private var mScrolledY = 0

    //手指最后触摸的位置
    private var mLastTouchY = 0f

    //手指按下时间，根据按下抬起时间差处理点击滚动
    private var mDownStartTime: Long = 0

    //是否强制停止滚动
    private var isForceFinishScroll = false

    //是否是快速滚动，快速滚动结束后跳转位置
    private var isFlingScroll = false

    //当前选中的下标
    private var mSelectedItemPosition = 0

    //当前滚动经过的下标
    private var mCurrentScrollPosition = 0

    //字体
    private var mIsBoldForSelectedItem = false

    //如果 mIsBoldForSelectedItem==true 则这个字体为未选中条目的字体
    private var mNormalTypeface: Typeface? = null

    //如果 mIsBoldForSelectedItem==true 则这个字体为选中条目的字体
    private var mBoldTypeface: Typeface? = null

    //监听器
    var mOnItemSelectedListener: OnItemSelectedListener? = null
    var mOnWheelChangedListener: OnWheelChangedListener? = null

    //音频
    private var mSoundHelper: SoundHelper? = null

    //是否开启音频效果
    private var isSoundEffect = false


    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        initAttrsAndDefault(context, attrs)
        initValue(context)
    }

    /**
     * 初始化自定义属性及默认值
     *
     * @param context 上下文
     * @param attrs   attrs
     */
    private fun initAttrsAndDefault(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView)
        mTextSize = typedArray.getDimension(R.styleable.WheelView_wv_textSize, DEFAULT_TEXT_SIZE)
        isAutoFitTextSize = typedArray.getBoolean(R.styleable.WheelView_wv_autoFitTextSize, false)
        mTextAlign = typedArray.getInt(R.styleable.WheelView_wv_textAlign, TEXT_ALIGN_CENTER)
        mTextBoundaryMargin = typedArray.getDimension(R.styleable.WheelView_wv_textBoundaryMargin,
                DEFAULT_TEXT_BOUNDARY_MARGIN)
        mTextColor = typedArray.getColor(R.styleable.WheelView_wv_normalItemTextColor, DEFAULT_NORMAL_TEXT_COLOR)
        mSelectedItemTextColor = typedArray.getColor(R.styleable.WheelView_wv_selectedItemTextColor, DEFAULT_SELECTED_TEXT_COLOR)
        mLineSpacing = typedArray.getDimension(R.styleable.WheelView_wv_lineSpacing, DEFAULT_LINE_SPACING)
        isIntegerNeedFormat = typedArray.getBoolean(R.styleable.WheelView_wv_integerNeedFormat, false)
        mIntegerFormat = typedArray.getString(R.styleable.WheelView_wv_integerFormat)
        if (TextUtils.isEmpty(mIntegerFormat)) {
            mIntegerFormat = DEFAULT_INTEGER_FORMAT
        }
        mVisibleItems = typedArray.getInt(R.styleable.WheelView_wv_visibleItems, DEFAULT_VISIBLE_ITEM)
        //跳转可见item为奇数
        mVisibleItems = adjustVisibleItems(mVisibleItems)
        mSelectedItemPosition = typedArray.getInt(R.styleable.WheelView_wv_selectedItemPosition, 0)
        //初始化滚动下标
        mCurrentScrollPosition = mSelectedItemPosition
        isCyclic = typedArray.getBoolean(R.styleable.WheelView_wv_cyclic, false)
        isShowDivider = typedArray.getBoolean(R.styleable.WheelView_wv_showDivider, false)
        mDividerType = typedArray.getInt(R.styleable.WheelView_wv_dividerType, DIVIDER_TYPE_FILL)
        mDividerSize = typedArray.getDimension(R.styleable.WheelView_wv_dividerHeight, DEFAULT_DIVIDER_HEIGHT)
        mDividerColor = typedArray.getColor(R.styleable.WheelView_wv_dividerColor, DEFAULT_SELECTED_TEXT_COLOR)
        mDividerPaddingForWrap = typedArray.getDimension(R.styleable.WheelView_wv_dividerPaddingForWrap, DEFAULT_TEXT_BOUNDARY_MARGIN)
        mDividerOffset = typedArray.getDimensionPixelOffset(R.styleable.WheelView_wv_dividerOffset, 0).toFloat()
        isDrawSelectedRect = typedArray.getBoolean(R.styleable.WheelView_wv_drawSelectedRect, false)
        mSelectedRectColor = typedArray.getColor(R.styleable.WheelView_wv_selectedRectColor, Color.TRANSPARENT)
        isCurved = typedArray.getBoolean(R.styleable.WheelView_wv_curved, true)
        mCurvedArcDirection = typedArray.getInt(R.styleable.WheelView_wv_curvedArcDirection, CURVED_ARC_DIRECTION_CENTER)
        mCurvedArcDirectionFactor = typedArray.getFloat(R.styleable.WheelView_wv_curvedArcDirectionFactor, DEFAULT_CURVED_FACTOR)
        //折射偏移默认值
        //Deprecated 将在新版中移除
        val curvedRefractRatio = typedArray.getFloat(R.styleable.WheelView_wv_curvedRefractRatio, 0.9f)
        mRefractRatio = typedArray.getFloat(R.styleable.WheelView_wv_refractRatio, DEFAULT_REFRACT_RATIO)
        mRefractRatio = if (isCurved) Math.min(curvedRefractRatio, mRefractRatio) else mRefractRatio
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO
        }
        typedArray.recycle()
    }

    /**
     * 初始化并设置默认值
     *
     * @param context 上下文
     */
    private fun initValue(context: Context) {
        val viewConfiguration = ViewConfiguration.get(context)
        mMaxFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity
        mMinFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity
        mScroller = Scroller(context)
        mDrawRect = Rect()
        mCamera = Camera()
        mMatrix = Matrix()
        if (!isInEditMode) {
            mSoundHelper = SoundHelper.obtain()
            initDefaultVolume(context)
        }
        calculateTextSize()
        updateTextAlign()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mSoundHelper != null) {
            mSoundHelper?.release()
        }
    }

    /**
     * 初始化默认音量
     *
     * @param context 上下文
     */
    private fun initDefaultVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager != null) {
            //获取系统媒体当前音量
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            //获取系统媒体最大音量
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            //设置播放音量
            mSoundHelper!!.playVolume = currentVolume * 1.0f / maxVolume
        } else {
            mSoundHelper!!.playVolume = 0.3f
        }
    }

    /**
     * 测量文字最大所占空间
     */
    private fun calculateTextSize() {
        mPaint.textSize = mTextSize
        for (i in mDataList.indices) {
            val textWidth = mPaint.measureText(mDataList[i]).toInt()
            mMaxTextWidth = Math.max(textWidth, mMaxTextWidth)
        }
        mFontMetrics = mPaint.fontMetrics
        //itemHeight实际等于字体高度+一个行间距
        mItemHeight = (mFontMetrics?.bottom!! - mFontMetrics?.top!! + mLineSpacing).toInt()
    }

    /**
     * 更新textAlign
     */
    private fun updateTextAlign() {
        when (mTextAlign) {
            TEXT_ALIGN_LEFT -> mPaint.textAlign = Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> mPaint.textAlign = Paint.Align.RIGHT
            TEXT_ALIGN_CENTER -> mPaint.textAlign = Paint.Align.CENTER
            else -> mPaint.textAlign = Paint.Align.CENTER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Line Space算在了mItemHeight中
        val height: Int
        height = if (isCurved) {
            (mItemHeight * mVisibleItems * 2 / Math.PI + paddingTop + paddingBottom).toInt()
        } else {
            mItemHeight * mVisibleItems + paddingTop + paddingBottom
        }
        var width = (mMaxTextWidth + paddingLeft + paddingRight + mTextBoundaryMargin * 2).toInt()
        if (isCurved) {
            val towardRange = (Math.sin(Math.PI / 48) * height).toInt()
            width += towardRange
        }
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                resolveSizeAndState(height, heightMeasureSpec, 0))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //设置内容可绘制区域
        mDrawRect!![paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom
        mCenterX = mDrawRect!!.centerX()
        mCenterY = mDrawRect!!.centerY()
        mSelectedItemTopLimit = (mCenterY - mItemHeight / 2 - mDividerOffset).toInt()
        mSelectedItemBottomLimit = (mCenterY + mItemHeight / 2 + mDividerOffset).toInt()
        mClipLeft = paddingLeft
        mClipTop = paddingTop
        mClipRight = width - paddingRight
        mClipBottom = height - paddingBottom
        calculateDrawStart()
        //计算滚动限制
        calculateLimitY()

        //如果初始化时有选中的下标，则计算选中位置的距离
        val itemDistance = calculateItemDistance(mSelectedItemPosition)
        if (itemDistance > 0) {
            doScroll(itemDistance)
        }
    }

    /**
     * 起算起始位置
     */
    private fun calculateDrawStart() {
        mStartX = when (mTextAlign) {
            TEXT_ALIGN_LEFT -> (paddingLeft + mTextBoundaryMargin).toInt()
            TEXT_ALIGN_RIGHT -> (width - paddingRight - mTextBoundaryMargin).toInt()
            TEXT_ALIGN_CENTER -> width / 2
            else -> width / 2
        }

        //文字中心距离baseline的距离
        mCenterToBaselineY = (mFontMetrics!!.ascent + (mFontMetrics!!.descent - mFontMetrics!!.ascent) / 2).toInt()
    }

    /**
     * 计算滚动限制
     */
    private fun calculateLimitY() {
        mMinScrollY = if (isCyclic) Int.MIN_VALUE else 0
        //下边界 (dataSize - 1 - mInitPosition) * mItemHeight
        mMaxScrollY = if (isCyclic) Int.MAX_VALUE else (mDataList.size - 1) * mItemHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //绘制选中区域
        drawSelectedRect(canvas)
        //绘制分割线
        drawDivider(canvas)

        //滚动了多少个item，滚动的Y值高度除去每行Item的高度
        val scrolledItem = mScrollOffsetY / dividedItemHeight()
        //没有滚动完一个item时的偏移值，平滑滑动
        val scrolledOffset = mScrollOffsetY % dividedItemHeight()
        //向上取整
        val halfItem = (mVisibleItems + 1) / 2
        //计算的最小index
        val minIndex: Int
        //计算的最大index
        val maxIndex: Int
        if (scrolledOffset < 0) {
            //小于0
            minIndex = scrolledItem - halfItem - 1
            maxIndex = scrolledItem + halfItem
        } else if (scrolledOffset > 0) {
            minIndex = scrolledItem - halfItem
            maxIndex = scrolledItem + halfItem + 1
        } else {
            minIndex = scrolledItem - halfItem
            maxIndex = scrolledItem + halfItem
        }

        //绘制item
        for (i in minIndex until maxIndex) {
            if (isCurved) {
                draw3DItem(canvas, i, scrolledOffset)
            } else {
                drawItem(canvas, i, scrolledOffset)
            }
        }
    }

    /**
     * 绘制选中区域
     *
     * @param canvas 画布
     */
    private fun drawSelectedRect(canvas: Canvas) {
        if (isDrawSelectedRect) {
            mPaint.color = mSelectedRectColor
            canvas.drawRect(mClipLeft.toFloat(), mSelectedItemTopLimit.toFloat(), mClipRight.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas 画布
     */
    private fun drawDivider(canvas: Canvas) {
        if (isShowDivider) {
            mPaint.color = mDividerColor
            val originStrokeWidth = mPaint.strokeWidth
            mPaint.strokeJoin = Paint.Join.ROUND
            mPaint.strokeCap = Paint.Cap.ROUND
            mPaint.strokeWidth = mDividerSize
            if (mDividerType == DIVIDER_TYPE_FILL) {
                canvas.drawLine(mClipLeft.toFloat(), mSelectedItemTopLimit.toFloat(), mClipRight.toFloat(), mSelectedItemTopLimit.toFloat(), mPaint)
                canvas.drawLine(mClipLeft.toFloat(), mSelectedItemBottomLimit.toFloat(), mClipRight.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
            } else {
                //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                val startX = (mCenterX - mMaxTextWidth / 2 - mDividerPaddingForWrap).toInt()
                val stopX = (mCenterX + mMaxTextWidth / 2 + mDividerPaddingForWrap).toInt()
                val wrapStartX = if (startX < mClipLeft) mClipLeft else startX
                val wrapStopX = if (stopX > mClipRight) mClipRight else stopX
                canvas.drawLine(wrapStartX.toFloat(), mSelectedItemTopLimit.toFloat(), wrapStopX.toFloat(), mSelectedItemTopLimit.toFloat(), mPaint)
                canvas.drawLine(wrapStartX.toFloat(), mSelectedItemBottomLimit.toFloat(), wrapStopX.toFloat(), mSelectedItemBottomLimit.toFloat(), mPaint)
            }
            mPaint.strokeWidth = originStrokeWidth
        }
    }

    /**
     * 绘制2D效果
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private fun drawItem(canvas: Canvas, index: Int, scrolledOffset: Int) {
        val text = getDataByIndex(index) ?: return

        //index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - mScrollOffsetY / dividedItemHeight()) * mItemHeight - scrolledOffset
        //记录初始测量的字体起始X
        val startX = mStartX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = if (isAutoFitTextSize) remeasureTextSize(text) else mCenterToBaselineY
        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.color = mSelectedItemTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.color = mSelectedItemTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
            mPaint.color = mTextColor
            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDraw2DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, item2CenterOffsetY, centerToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.color = mSelectedItemTextColor
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
            mPaint.color = mTextColor
            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDraw2DText(canvas, text, mClipTop, mSelectedItemTopLimit, item2CenterOffsetY, centerToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            mPaint.color = mTextColor
            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDraw2DText(canvas, text, mClipTop, mClipBottom, item2CenterOffsetY, centerToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }
        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mPaint.textSize = mTextSize
            mStartX = startX
        }
    }

    /**
     * 裁剪并绘制2d text
     *
     * @param canvas             画布
     * @param text               绘制的文字
     * @param clipTop            裁剪的上边界
     * @param clipBottom         裁剪的下边界
     * @param item2CenterOffsetY 距离中间项的偏移
     * @param centerToBaselineY  文字中心距离baseline的距离
     */
    private fun clipAndDraw2DText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int,
                                  item2CenterOffsetY: Int, centerToBaselineY: Int) {
        canvas.save()
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom)
        canvas.drawText(text, 0, text.length, mStartX.toFloat(), mCenterY + item2CenterOffsetY - centerToBaselineY.toFloat(), mPaint)
        canvas.restore()
    }

    /**
     * 重新测量字体大小
     *
     * @param contentText 被测量文字内容
     * @return 文字中心距离baseline的距离
     */
    private fun remeasureTextSize(contentText: String): Int {
        var textWidth = mPaint.measureText(contentText)
        var drawWidth = width.toFloat()
        var textMargin = mTextBoundaryMargin * 2
        //稍微增加了一点文字边距 最大为宽度的1/10
        if (textMargin > drawWidth / 10f) {
            drawWidth = drawWidth * 9f / 10f
            textMargin = drawWidth / 10f
        } else {
            drawWidth = drawWidth - textMargin
        }
        if (drawWidth <= 0) {
            return mCenterToBaselineY
        }
        var textSize = mTextSize
        while (textWidth > drawWidth) {
            textSize--
            if (textSize <= 0) {
                break
            }
            mPaint.textSize = textSize
            textWidth = mPaint.measureText(contentText)
        }
        //重新计算文字起始X
        recalculateStartX(textMargin / 2.0f)
        //高度起点也变了
        return recalculateCenterToBaselineY()
    }

    /**
     * 重新计算字体起始X
     *
     * @param textMargin 文字外边距
     */
    private fun recalculateStartX(textMargin: Float) {
        mStartX = when (mTextAlign) {
            TEXT_ALIGN_LEFT -> textMargin.toInt()
            TEXT_ALIGN_RIGHT -> (width - textMargin).toInt()
            TEXT_ALIGN_CENTER -> width / 2
            else -> width / 2
        }
    }

    /**
     * 字体大小变化后重新计算距离基线的距离
     *
     * @return 文字中心距离baseline的距离
     */
    private fun recalculateCenterToBaselineY(): Int {
        val fontMetrics = mPaint.fontMetrics
        //高度起点也变了
        return (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2).toInt()
    }

    /**
     * 绘制弯曲（3D）效果的item
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private fun draw3DItem(canvas: Canvas, index: Int, scrolledOffset: Int) {
        val text = getDataByIndex(index) ?: return
        // 滚轮的半径
        val radius = (height - paddingTop - paddingBottom) / 2
        //index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - mScrollOffsetY / dividedItemHeight()) * mItemHeight - scrolledOffset

        // 当滑动的角度和y轴垂直时（此时文字已经显示为一条线），不绘制文字
        if (Math.abs(item2CenterOffsetY) > radius * Math.PI / 2) return
        val angle = item2CenterOffsetY.toDouble() / radius
        // 绕x轴滚动的角度
        val rotateX = Math.toDegrees(-angle).toFloat()
        // 滚动的距离映射到y轴的长度
        val translateY = (Math.sin(angle) * radius).toFloat()
        // 滚动的距离映射到z轴的长度
        val translateZ = ((1 - Math.cos(angle)) * radius).toFloat()
        // 透明度
        val alpha = (Math.cos(angle) * 255).toInt()

        //记录初始测量的字体起始X
        val startX = mStartX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = if (isAutoFitTextSize) remeasureTextSize(text) else mCenterToBaselineY
        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.color = mSelectedItemTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.color = mSelectedItemTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)
            mPaint.color = mTextColor
            mPaint.alpha = alpha
            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mSelectedItemBottomLimit, mClipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.color = mSelectedItemTextColor
            mPaint.alpha = 255
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)
            mPaint.color = mTextColor
            mPaint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mClipTop, mSelectedItemTopLimit,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            mPaint.color = mTextColor
            mPaint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = mPaint.textSize
            mPaint.textSize = textSize * mRefractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDraw3DText(canvas, text, mClipTop, mClipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }
        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mPaint.textSize = mTextSize
            mStartX = startX
        }
    }

    /**
     * 裁剪并绘制弯曲（3D）效果
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param clipTop           裁剪的上边界
     * @param clipBottom        裁剪的下边界
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private fun clipAndDraw3DText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int,
                                  rotateX: Float, offsetY: Float, offsetZ: Float, centerToBaselineY: Int) {
        canvas.save()
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom)
        draw3DText(canvas, text, rotateX, offsetY, offsetZ, centerToBaselineY)
        canvas.restore()
    }

    /**
     * 绘制弯曲（3D）的文字
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private fun draw3DText(canvas: Canvas, text: String, rotateX: Float, offsetY: Float,
                           offsetZ: Float, centerToBaselineY: Int) {
        mCamera!!.save()
        mCamera!!.translate(0f, 0f, offsetZ)
        mCamera!!.rotateX(rotateX)
        mCamera!!.getMatrix(mMatrix)
        mCamera!!.restore()

        // 调节中心点
        var centerX = mCenterX.toFloat()
        //根据弯曲（3d）对齐方式设置系数
        if (mCurvedArcDirection == CURVED_ARC_DIRECTION_LEFT) {
            centerX = mCenterX * (1 + mCurvedArcDirectionFactor)
        } else if (mCurvedArcDirection == CURVED_ARC_DIRECTION_RIGHT) {
            centerX = mCenterX * (1 - mCurvedArcDirectionFactor)
        }
        val centerY = mCenterY + offsetY
        mMatrix!!.preTranslate(-centerX, -centerY)
        mMatrix!!.postTranslate(centerX, centerY)
        canvas.concat(mMatrix)
        canvas.drawText(text, 0, text.length, mStartX.toFloat(), centerY - centerToBaselineY, mPaint)
    }

    private fun changeTypefaceIfBoldForSelectedItem() {
        if (mIsBoldForSelectedItem) {
            mPaint.typeface = mNormalTypeface
        }
    }

    private fun resetTypefaceIfBoldForSelectedItem() {
        if (mIsBoldForSelectedItem) {
            mPaint.typeface = mBoldTypeface
        }
    }

    /**
     * 根据下标获取到内容
     *
     * @param index 下标
     * @return 绘制的文字内容
     */
    private fun getDataByIndex(index: Int): String? {
        val dataSize = mDataList.size
        if (dataSize == 0) {
            return null
        }
        var itemText: String? = null
        if (isCyclic) {
            var i = index % dataSize
            if (i < 0) {
                i += dataSize
            }
            itemText = mDataList[i]
        } else {
            if (index >= 0 && index < dataSize) {
                itemText = mDataList[index]
            }
        }
        return itemText
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //屏蔽如果未设置数据时，触摸导致运算数据不正确的崩溃 issue #20
        if (!isEnabled || mDataList.isEmpty()) {
            return super.onTouchEvent(event)
        }
        initVelocityTracker()
        mVelocityTracker!!.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //手指按下
                //处理滑动事件嵌套 拦截事件序列
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                //如果未滚动完成，强制滚动完成
                if (!mScroller!!.isFinished) {
                    //强制滚动完成
                    mScroller!!.forceFinished(true)
                    isForceFinishScroll = true
                }
                mLastTouchY = event.y
                //按下时间
                mDownStartTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                //手指移动
                val moveY = event.y
                val deltaY = moveY - mLastTouchY
                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING)
                }
                onWheelScrollStateChanged(SCROLL_STATE_DRAGGING)
                if (Math.abs(deltaY) > 1) {
                    //deltaY 上滑为正，下滑为负
                    doScroll((-deltaY).toInt())
                    mLastTouchY = moveY
                    invalidateIfYChanged()
                }
            }
            MotionEvent.ACTION_UP -> {
                //手指抬起
                isForceFinishScroll = false
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                val velocityY = mVelocityTracker!!.yVelocity
                if (Math.abs(velocityY) > mMinFlingVelocity) {
                    //快速滑动
                    mScroller!!.forceFinished(true)
                    isFlingScroll = true
                    mScroller!!.fling(0, mScrollOffsetY, 0, (-velocityY).toInt(), 0, 0,
                            mMinScrollY, mMaxScrollY)
                } else {
                    var clickToCenterDistance = 0
                    if (System.currentTimeMillis() - mDownStartTime <= DEFAULT_CLICK_CONFIRM) {
                        //处理点击滚动
                        //手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (event.y - mCenterY).toInt()
                    }
                    val scrollRange = clickToCenterDistance +
                            calculateDistanceToEndPoint((mScrollOffsetY + clickToCenterDistance) % dividedItemHeight())
                    //大于最小值滚动值
                    val isInMinRange = scrollRange < 0 && mScrollOffsetY + scrollRange >= mMinScrollY
                    //小于最大滚动值
                    val isInMaxRange = scrollRange > 0 && mScrollOffsetY + scrollRange <= mMaxScrollY
                    if (isInMinRange || isInMaxRange) {
                        //在滚动范围之内再修正位置
                        //平稳滑动
                        mScroller!!.startScroll(0, mScrollOffsetY, 0, scrollRange)
                    }
                }
                invalidateIfYChanged()
                ViewCompat.postOnAnimation(this, this)
                //回收 VelocityTracker
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL ->                 //事件被终止
                //回收
                recycleVelocityTracker()
        }
        return true
    }

    /**
     * 初始化 VelocityTracker
     */
    private fun initVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    /**
     * 回收 VelocityTracker
     */
    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    /**
     * 计算滚动偏移
     *
     * @param distance 滚动距离
     */
    private fun doScroll(distance: Int) {
        mScrollOffsetY += distance
        if (!isCyclic) {
            //修正边界
            if (mScrollOffsetY < mMinScrollY) {
                mScrollOffsetY = mMinScrollY
            } else if (mScrollOffsetY > mMaxScrollY) {
                mScrollOffsetY = mMaxScrollY
            }
        }
    }

    /**
     * 当Y轴的偏移值改变时再重绘，减少重回次数
     */
    private fun invalidateIfYChanged() {
        if (mScrollOffsetY != mScrolledY) {
            mScrolledY = mScrollOffsetY
            //滚动偏移发生变化
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener?.onWheelScroll(mScrollOffsetY)
            }
            onWheelScroll(mScrollOffsetY)
            //观察item变化
            observeItemChanged()
            invalidate()
        }
    }

    /**
     * 观察item改变
     */
    private fun observeItemChanged() {
        //item改变回调
        val oldPosition = mCurrentScrollPosition
        val newPosition = getCurrentPosition()
        if (oldPosition != newPosition) {
            //改变了
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener?.onWheelItemChanged(oldPosition, newPosition)
            }
            onWheelItemChanged(oldPosition, newPosition)
            //播放音频
            playSoundEffect()
            //更新下标
            mCurrentScrollPosition = newPosition
        }
    }

    /**
     * 播放滚动音效
     */
    fun playSoundEffect() {
        if (mSoundHelper != null && isSoundEffect) {
            mSoundHelper?.playSoundEffect()
        }
    }

    /**
     * 强制滚动完成，直接停止
     */
    fun forceFinishScroll() {
        if (!mScroller!!.isFinished) {
            mScroller!!.forceFinished(true)
        }
    }

    /**
     * 强制滚动完成，并且直接滚动到最终位置
     */
    fun abortFinishScroll() {
        if (!mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return 偏移量
     */
    private fun calculateDistanceToEndPoint(remainder: Int): Int {
        return if (Math.abs(remainder) > mItemHeight / 2) {
            if (mScrollOffsetY < 0) {
                -mItemHeight - remainder
            } else {
                mItemHeight - remainder
            }
        } else {
            -remainder
        }
    }

    /**
     * 使用run方法而不是computeScroll是因为，invalidate也会执行computeScroll导致回调执行不准确
     */
    override fun run() {
        //停止滚动更新当前下标
        if (mScroller!!.isFinished && !isForceFinishScroll && !isFlingScroll) {
            if (mItemHeight == 0) return
            //滚动状态停止
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_IDLE)
            }
            onWheelScrollStateChanged(SCROLL_STATE_IDLE)
            val currentItemPosition = getCurrentPosition()
            //当前选中的Position没变时不回调 onItemSelected()
            if (currentItemPosition == mSelectedItemPosition) {
                return
            }
            mSelectedItemPosition = currentItemPosition
            //停止后重新赋值
            mCurrentScrollPosition = mSelectedItemPosition

            //停止滚动，选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener?.onItemSelected(this, mDataList[mSelectedItemPosition], mSelectedItemPosition)
            }
            onItemSelected(mDataList[mSelectedItemPosition], mSelectedItemPosition)
            //滚动状态回调
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener?.onWheelSelected(mSelectedItemPosition)
            }
            onWheelSelected(mSelectedItemPosition)
        }
        if (mScroller!!.computeScrollOffset()) {
            val oldY = mScrollOffsetY
            mScrollOffsetY = mScroller!!.currY
            if (oldY != mScrollOffsetY) {
                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener?.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING)
                }
                onWheelScrollStateChanged(SCROLL_STATE_SCROLLING)
            }
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else if (isFlingScroll) {
            //滚动完成后，根据是否为快速滚动处理是否需要调整最终位置
            isFlingScroll = false
            //快速滚动后需要调整滚动完成后的最终位置，重新启动scroll滑动到中心位置
            mScroller!!.startScroll(0, mScrollOffsetY, 0, calculateDistanceToEndPoint(mScrollOffsetY % dividedItemHeight()))
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return 偏移量对应的当前下标 if dataList is empty return -1
     */
    private fun getCurrentPosition(): Int {
        if (mDataList.isEmpty()) {
            return -1
        }
        val itemPosition: Int
        itemPosition = if (mScrollOffsetY < 0) {
            (mScrollOffsetY - mItemHeight / 2) / dividedItemHeight()
        } else {
            (mScrollOffsetY + mItemHeight / 2) / dividedItemHeight()
        }
        var currentPosition = itemPosition % mDataList.size
        if (currentPosition < 0) {
            currentPosition += mDataList.size
        }
        return currentPosition
    }

    /**
     * mItemHeight 为被除数时避免为0
     *
     * @return 被除数不为0
     */
    private fun dividedItemHeight(): Int {
        return if (mItemHeight > 0) mItemHeight else 1
    }

    /**
     * 获取音效开关状态
     *
     * @return 是否开启滚动音效
     */
    fun isSoundEffect(): Boolean {
        return isSoundEffect
    }

    /**
     * 设置音效开关
     *
     * @param isSoundEffect 是否开启滚动音效
     */
    fun setSoundEffect(isSoundEffect: Boolean) {
        this.isSoundEffect = isSoundEffect
    }

    /**
     * 设置声音效果资源
     *
     * @param rawResId 声音效果资源 越小效果越好 [RawRes]
     */
    fun setSoundEffectResource(@RawRes rawResId: Int) {
        mSoundHelper?.load(context, rawResId)
    }

    /**
     * 获取播放音量
     *
     * @return 播放音量 range 0.0-1.0
     */
    fun getPlayVolume(): Float {
        return mSoundHelper?.playVolume ?: 0f
    }

    /**
     * 设置播放音量
     *
     * @param playVolume 播放音量 range 0.0-1.0
     */
    fun setPlayVolume(@FloatRange(from = 0.0, to = 1.0) playVolume: Float) {
        mSoundHelper?.playVolume = playVolume
    }

    /**
     * 获取指定 position 的数据
     *
     * @param position 下标
     * @return position 对应的数据 [Nullable]
     */
    fun getItemData(position: Int): String? {
        if (isPositionInRange(position)) {
            return mDataList[position]
        } else if (mDataList.size > 0 && position >= mDataList.size) {
            return mDataList[mDataList.size - 1]
        } else if (mDataList.size > 0 && position < 0) {
            return mDataList[0]
        }
        return null
    }

    /**
     * 获取当前选中的item数据
     *
     * @return 当前选中的item数据
     */
    fun getSelectedItemData(): String? {
        return getItemData(mSelectedItemPosition)
    }


    /**
     * 设置数据
     *
     * @param dataList 数据列表
     */
    fun setData(dataList: List<String>?) {
        if (dataList == null) {
            return
        }
        mDataList = dataList
        if (!isResetSelectedPosition && mDataList.size > 0) {
            //不重置选中下标
            if (mSelectedItemPosition >= mDataList.size) {
                mSelectedItemPosition = mDataList.size - 1
                //重置滚动下标
                mCurrentScrollPosition = mSelectedItemPosition
            }
        } else {
            //重置选中下标和滚动下标
            mSelectedItemPosition = 0
            mCurrentScrollPosition = mSelectedItemPosition
        }
        //强制滚动完成
        forceFinishScroll()
        calculateTextSize()
        calculateLimitY()
        //重置滚动偏移
        mScrollOffsetY = mSelectedItemPosition * mItemHeight
        requestLayout()
        invalidate()
    }

    /**
     * 当数据变化时，是否重置选中下标到第一个
     *
     * @return 是否重置选中下标到第一个
     */
    fun isResetSelectedPosition(): Boolean {
        return isResetSelectedPosition
    }

    /**
     * 设置当数据变化时，是否重置选中下标到第一个
     *
     * @param isResetSelectedPosition 当数据变化时,是否重置选中下标到第一个
     */
    fun setResetSelectedPosition(isResetSelectedPosition: Boolean) {
        this.isResetSelectedPosition = isResetSelectedPosition
    }

    /**
     * 获取字体大小
     *
     * @return 字体大小
     */
    fun getTextSize(): Float {
        return mTextSize
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     */
    fun setTextSize(textSize: Float) {
        setTextSize(textSize, false)
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     * @param isSp     单位是否是 sp
     */
    fun setTextSize(textSize: Float, isSp: Boolean) {
        val tempTextSize = mTextSize
        mTextSize = if (isSp) sp2px(textSize) else textSize
        if (tempTextSize == mTextSize) {
            return
        }
        //强制滚动完成
        forceFinishScroll()
        calculateTextSize()
        calculateDrawStart()
        calculateLimitY()
        //字体大小变化，偏移距离也变化了
        mScrollOffsetY = mSelectedItemPosition * mItemHeight
        requestLayout()
        invalidate()
    }

    /**
     * 获取是否自动调整字体大小，以显示完全
     *
     * @return 是否自动调整字体大小
     */
    fun isAutoFitTextSize(): Boolean {
        return isAutoFitTextSize
    }

    /**
     * 设置是否自动调整字体大小，以显示完全
     *
     * @param isAutoFitTextSize 是否自动调整字体大小
     */
    fun setAutoFitTextSize(isAutoFitTextSize: Boolean) {
        this.isAutoFitTextSize = isAutoFitTextSize
        invalidate()
    }

    /**
     * 获取当前字体
     *
     * @return 字体
     */
    fun getTypeface(): Typeface? {
        return mPaint.typeface
    }

    /**
     * 设置当前字体
     *
     * @param typeface 字体
     */
    fun setTypeface(typeface: Typeface?) {
        setTypeface(typeface, false)
    }

    /**
     * 设置当前字体
     *
     * @param typeface              字体
     * @param isBoldForSelectedItem 是否设置选中条目字体加粗，其他条目不会加粗
     */
    fun setTypeface(typeface: Typeface?, isBoldForSelectedItem: Boolean) {
        if (typeface == null || mPaint.typeface === typeface) {
            return
        }
        //强制滚动完成
        forceFinishScroll()
        mIsBoldForSelectedItem = isBoldForSelectedItem
        if (mIsBoldForSelectedItem) {
            //如果设置了选中条目字体加粗，其他条目不会加粗，则拆分两份字体
            if (typeface.isBold) {
                mNormalTypeface = Typeface.create(typeface, Typeface.NORMAL)
                mBoldTypeface = typeface
            } else {
                mNormalTypeface = typeface
                mBoldTypeface = Typeface.create(typeface, Typeface.BOLD)
            }
            //测量时 使用加粗字体测量，因为加粗字体比普通字体宽，以大的为准进行测量
            mPaint.typeface = mBoldTypeface
        } else {
            mPaint.typeface = typeface
        }
        calculateTextSize()
        calculateDrawStart()
        //字体大小变化，偏移距离也变化了
        mScrollOffsetY = mSelectedItemPosition * mItemHeight
        calculateLimitY()
        requestLayout()
        invalidate()
    }

    /**
     * 获取文字对齐方式
     *
     * @return 文字对齐
     * [.TEXT_ALIGN_LEFT]
     * [.TEXT_ALIGN_CENTER]
     * [.TEXT_ALIGN_RIGHT]
     */
    fun getTextAlign(): Int {
        return mTextAlign
    }

    /**
     * 设置文字对齐方式
     *
     * @param textAlign 文字对齐方式
     * [.TEXT_ALIGN_LEFT]
     * [.TEXT_ALIGN_CENTER]
     * [.TEXT_ALIGN_RIGHT]
     */
    fun setTextAlign(textAlign: Int) {
        if (mTextAlign == textAlign) {
            return
        }
        mTextAlign = textAlign
        updateTextAlign()
        calculateDrawStart()
        invalidate()
    }

    /**
     * 获取未选中条目颜色
     *
     * @return 未选中条目颜色 ColorInt
     */
    fun getNormalItemTextColor(): Int {
        return mTextColor
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColorRes 未选中条目颜色 [ColorRes]
     */
    fun setNormalItemTextColorRes(@ColorRes textColorRes: Int) {
        setNormalItemTextColor(ContextCompat.getColor(context, textColorRes))
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColor 未选中条目颜色 [ColorInt]
     */
    fun setNormalItemTextColor(@ColorInt textColor: Int) {
        if (mTextColor == textColor) {
            return
        }
        mTextColor = textColor
        invalidate()
    }

    /**
     * 获取选中条目颜色
     *
     * @return 选中条目颜色 ColorInt
     */
    fun getSelectedItemTextColor(): Int {
        return mSelectedItemTextColor
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemColorRes 选中条目颜色 [ColorRes]
     */
    fun setSelectedItemTextColorRes(@ColorRes selectedItemColorRes: Int) {
        setSelectedItemTextColor(ContextCompat.getColor(context, selectedItemColorRes))
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemTextColor 选中条目颜色 [ColorInt]
     */
    fun setSelectedItemTextColor(@ColorInt selectedItemTextColor: Int) {
        if (mSelectedItemTextColor == selectedItemTextColor) {
            return
        }
        mSelectedItemTextColor = selectedItemTextColor
        invalidate()
    }

    /**
     * 获取文字距离边界的外边距
     *
     * @return 外边距值
     */
    fun getTextBoundaryMargin(): Float {
        return mTextBoundaryMargin
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     */
    fun setTextBoundaryMargin(textBoundaryMargin: Float) {
        setTextBoundaryMargin(textBoundaryMargin, false)
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     * @param isDp               单位是否为 dp
     */
    fun setTextBoundaryMargin(textBoundaryMargin: Float, isDp: Boolean) {
        val tempTextBoundaryMargin = mTextBoundaryMargin
        mTextBoundaryMargin = if (isDp) dp2px(textBoundaryMargin) else textBoundaryMargin
        if (tempTextBoundaryMargin == mTextBoundaryMargin) {
            return
        }
        requestLayout()
        invalidate()
    }

    /**
     * 获取item间距
     *
     * @return 行间距值
     */
    fun getLineSpacing(): Float {
        return mLineSpacing
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     */
    fun setLineSpacing(lineSpacing: Float) {
        setLineSpacing(lineSpacing, false)
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     * @param isDp        lineSpacing 单位是否为 dp
     */
    fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        val tempLineSpace = mLineSpacing
        mLineSpacing = if (isDp) dp2px(lineSpacing) else lineSpacing
        if (tempLineSpace == mLineSpacing) {
            return
        }
        mScrollOffsetY = 0
        calculateTextSize()
        requestLayout()
        invalidate()
    }

    /**
     * 获取数据为Integer类型时是否需要转换
     *
     * @return isIntegerNeedFormat
     */
    fun isIntegerNeedFormat(): Boolean {
        return isIntegerNeedFormat
    }

    /**
     * 设置数据为Integer类型时是否需要转换
     *
     * @param isIntegerNeedFormat 数据为Integer类型时是否需要转换
     */
    fun setIntegerNeedFormat(isIntegerNeedFormat: Boolean) {
        if (this.isIntegerNeedFormat == isIntegerNeedFormat) {
            return
        }
        this.isIntegerNeedFormat = isIntegerNeedFormat
        calculateTextSize()
        requestLayout()
        invalidate()
    }

    /**
     * 同时设置 isIntegerNeedFormat=true 和 mIntegerFormat=integerFormat
     *
     * @param integerFormat 注意：integerFormat 中必须包含并且只能包含一个格式说明符（format specifier）
     * 格式说明符请参照 http://java2s.com/Tutorials/Java/Data_Format/Java_Format_Specifier.htm
     *
     *
     * 如果有多个格式说明符会抛出 java.util.MissingFormatArgumentException: Format specifier '%s'(多出来的说明符)
     */
    fun setIntegerNeedFormat(integerFormat: String) {
        isIntegerNeedFormat = true
        mIntegerFormat = integerFormat
        calculateTextSize()
        requestLayout()
        invalidate()
    }

    /**
     * 获取Integer类型转换格式
     *
     * @return integerFormat
     */
    fun getIntegerFormat(): String? {
        return mIntegerFormat
    }

    /**
     * 设置Integer类型转换格式
     *
     * @param integerFormat 注意：integerFormat 中必须包含并且只能包含一个格式说明符（format specifier）
     * 格式说明符请参照 http://java2s.com/Tutorials/Java/Data_Format/Java_Format_Specifier.htm
     *
     *
     * 如果有多个格式说明符会抛出 java.util.MissingFormatArgumentException: Format specifier '%s'(多出来的说明符)
     */
    fun setIntegerFormat(integerFormat: String) {
        if (TextUtils.isEmpty(integerFormat) || integerFormat == mIntegerFormat) {
            return
        }
        mIntegerFormat = integerFormat
        calculateTextSize()
        requestLayout()
        invalidate()
    }

    /**
     * 获取可见条目数
     *
     * @return 可见条目数
     */
    fun getVisibleItems(): Int {
        return mVisibleItems
    }

    /**
     * 设置可见的条目数
     *
     * @param visibleItems 可见条目数
     */
    fun setVisibleItems(visibleItems: Int) {
        if (mVisibleItems == visibleItems) {
            return
        }
        mVisibleItems = adjustVisibleItems(visibleItems)
        mScrollOffsetY = 0
        requestLayout()
        invalidate()
    }

    /**
     * 跳转可见条目数为奇数
     *
     * @param visibleItems 可见条目数
     * @return 调整后的可见条目数
     */
    private fun adjustVisibleItems(visibleItems: Int): Int {
        return Math.abs(visibleItems / 2 * 2 + 1) // 当传入的值为偶数时,换算成奇数;
    }

    /**
     * 是否是循环滚动
     *
     * @return 是否是循环滚动
     */
    fun isCyclic(): Boolean {
        return isCyclic
    }

    /**
     * 设置是否循环滚动
     *
     * @param isCyclic 是否是循环滚动
     */
    fun setCyclic(isCyclic: Boolean) {
        if (this.isCyclic == isCyclic) {
            return
        }
        this.isCyclic = isCyclic
        forceFinishScroll()
        calculateLimitY()
        //设置当前选中的偏移值
        mScrollOffsetY = mSelectedItemPosition * mItemHeight
        invalidate()
    }

    /**
     * 获取当前选中下标
     *
     * @return 当前选中的下标
     */
    fun getSelectedItemPosition(): Int {
        return mSelectedItemPosition
    }

    /**
     * 设置当前选中下标
     *
     * @param position 下标
     */
    fun setSelectedItemPosition(position: Int) {
        setSelectedItemPosition(position, false)
    }

    /**
     * 设置当前选中下标
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     */
    fun setSelectedItemPosition(position: Int, isSmoothScroll: Boolean) {
        setSelectedItemPosition(position, isSmoothScroll, 0)
    }

    /**
     * 设置当前选中下标
     *
     *
     * bug 修复记录：调用这个方法时大多数情况在初始化时，如果没有执行 onSizeChanged() 方法时，调用这个方法会导致失效
     * 因为 onSizeChanged() 方法执行结束才确定边界等信息，
     * 所以在 onSizeChanged() 方法增加了兼容，如果 mSelectedItemPosition >0 的情况重新计算一下滚动值。
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动时间
     */
    fun setSelectedItemPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        if (!isPositionInRange(position)) {
            return
        }

        //item之间差值
        val itemDistance = calculateItemDistance(position)
        if (itemDistance == 0) {
            //如果最开始设置了下标为0，则itemDistance==0
            if (position != mSelectedItemPosition) {
                mSelectedItemPosition = position
                //选中条目回调
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener?.onItemSelected(this, mDataList[mSelectedItemPosition], mSelectedItemPosition)
                }
                onItemSelected(mDataList[mSelectedItemPosition], mSelectedItemPosition)
                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener?.onWheelSelected(mSelectedItemPosition)
                }
                onWheelSelected(mSelectedItemPosition)
            }
            return
        }
        //如果Scroller滑动未停止，强制结束动画
        abortFinishScroll()
        if (isSmoothScroll) {
            //如果是平滑滚动并且之前的Scroll滚动完成
            mScroller!!.startScroll(0, mScrollOffsetY, 0, itemDistance,
                    if (smoothDuration > 0) smoothDuration else DEFAULT_SCROLL_DURATION)
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else {
            doScroll(itemDistance)
            mSelectedItemPosition = position
            //选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener?.onItemSelected(this, mDataList[mSelectedItemPosition], mSelectedItemPosition)
            }
            onItemSelected(mDataList[mSelectedItemPosition], mSelectedItemPosition)
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener?.onWheelSelected(mSelectedItemPosition)
            }
            onWheelSelected(mSelectedItemPosition)
            invalidateIfYChanged()
        }
    }

    private fun calculateItemDistance(position: Int): Int {
        return position * mItemHeight - mScrollOffsetY
    }

    /**
     * 判断下标是否在数据列表范围内
     *
     * @param position 下标
     * @return 是否在数据列表范围内
     */
    fun isPositionInRange(position: Int): Boolean {
        return position >= 0 && position < mDataList.size
    }

    /**
     * 获取是否显示分割线
     *
     * @return 是否显示分割线
     */
    fun isShowDivider(): Boolean {
        return isShowDivider
    }

    /**
     * 设置是否显示分割线
     *
     * @param isShowDivider 是否显示分割线
     */
    fun setShowDivider(isShowDivider: Boolean) {
        if (this.isShowDivider == isShowDivider) {
            return
        }
        this.isShowDivider = isShowDivider
        invalidate()
    }

    /**
     * 获取分割线颜色
     *
     * @return 分割线颜色 ColorInt
     */
    fun getDividerColor(): Int {
        return mDividerColor
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColorRes 分割线颜色 [ColorRes]
     */
    fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        setDividerColor(ContextCompat.getColor(context, dividerColorRes))
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor 分割线颜色 [ColorInt]
     */
    fun setDividerColor(@ColorInt dividerColor: Int) {
        if (mDividerColor == dividerColor) {
            return
        }
        mDividerColor = dividerColor
        invalidate()
    }

    /**
     * 获取分割线高度
     *
     * @return 分割线高度
     */
    fun getDividerHeight(): Float {
        return mDividerSize
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     */
    fun setDividerHeight(dividerHeight: Float) {
        setDividerHeight(dividerHeight, false)
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     * @param isDp          单位是否是 dp
     */
    fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        val tempDividerHeight = mDividerSize
        mDividerSize = if (isDp) dp2px(dividerHeight) else dividerHeight
        if (tempDividerHeight == mDividerSize) {
            return
        }
        invalidate()
    }

    /**
     * 获取分割线填充类型
     *
     * @return 分割线填充类型
     * [.DIVIDER_TYPE_FILL]
     * [.DIVIDER_TYPE_WRAP]
     */
    fun getDividerType(): Int {
        return mDividerType
    }

    /**
     * 设置分割线填充类型
     *
     * @param dividerType 分割线填充类型
     * [.DIVIDER_TYPE_FILL]
     * [.DIVIDER_TYPE_WRAP]
     */
    fun setDividerType(dividerType: Int) {
        if (mDividerType == dividerType) {
            return
        }
        mDividerType = dividerType
        invalidate()
    }

    /**
     * 获取自适应分割线类型时的分割线内边距
     *
     * @return 分割线内边距
     */
    fun getDividerPaddingForWrap(): Float {
        return mDividerPaddingForWrap
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     */
    fun setDividerPaddingForWrap(dividerPaddingForWrap: Float) {
        setDividerPaddingForWrap(dividerPaddingForWrap, false)
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     * @param isDp                  单位是否是 dp
     */
    fun setDividerPaddingForWrap(dividerPaddingForWrap: Float, isDp: Boolean) {
        val tempDividerPadding = mDividerPaddingForWrap
        mDividerPaddingForWrap = if (isDp) dp2px(dividerPaddingForWrap) else dividerPaddingForWrap
        if (tempDividerPadding == mDividerPaddingForWrap) {
            return
        }
        invalidate()
    }

    /**
     * 获取分割线两端形状
     *
     * @return 分割线两端形状
     * [Paint.Cap.BUTT]
     * [Paint.Cap.ROUND]
     * [Paint.Cap.SQUARE]
     */
    fun getDividerCap(): Paint.Cap? {
        return mDividerCap
    }

    /**
     * 设置分割线两端形状
     *
     * @param dividerCap 分割线两端形状
     * [Paint.Cap.BUTT]
     * [Paint.Cap.ROUND]
     * [Paint.Cap.SQUARE]
     */
    fun setDividerCap(dividerCap: Paint.Cap) {
        if (mDividerCap == dividerCap) {
            return
        }
        mDividerCap = dividerCap
        invalidate()
    }

    /**
     * 获取是否绘制选中区域
     *
     * @return 是否绘制选中区域
     */
    fun isDrawSelectedRect(): Boolean {
        return isDrawSelectedRect
    }

    /**
     * 设置是否绘制选中区域
     *
     * @param isDrawSelectedRect 是否绘制选中区域
     */
    fun setDrawSelectedRect(isDrawSelectedRect: Boolean) {
        this.isDrawSelectedRect = isDrawSelectedRect
        invalidate()
    }

    /**
     * 获取选中区域颜色
     *
     * @return 选中区域颜色 ColorInt
     */
    fun getSelectedRectColor(): Int {
        return mSelectedRectColor
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColorRes 选中区域颜色 [ColorRes]
     */
    fun setSelectedRectColorRes(@ColorRes selectedRectColorRes: Int) {
        setSelectedRectColor(ContextCompat.getColor(context, selectedRectColorRes))
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColor 选中区域颜色 [ColorInt]
     */
    fun setSelectedRectColor(@ColorInt selectedRectColor: Int) {
        mSelectedRectColor = selectedRectColor
        invalidate()
    }

    /**
     * 获取是否是弯曲（3D）效果
     *
     * @return 是否是弯曲（3D）效果
     */
    fun isCurved(): Boolean {
        return isCurved
    }

    /**
     * 设置是否是弯曲（3D）效果
     *
     * @param isCurved 是否是弯曲（3D）效果
     */
    fun setCurved(isCurved: Boolean) {
        if (this.isCurved == isCurved) {
            return
        }
        this.isCurved = isCurved
        calculateTextSize()
        requestLayout()
        invalidate()
    }

    /**
     * 获取弯曲（3D）效果左右圆弧效果方向
     *
     * @return 左右圆弧效果方向
     * [.CURVED_ARC_DIRECTION_LEFT]
     * [.CURVED_ARC_DIRECTION_CENTER]
     * [.CURVED_ARC_DIRECTION_RIGHT]
     */
    fun getCurvedArcDirection(): Int {
        return mCurvedArcDirection
    }

    /**
     * 设置弯曲（3D）效果左右圆弧效果方向
     *
     * @param curvedArcDirection 左右圆弧效果方向
     * [.CURVED_ARC_DIRECTION_LEFT]
     * [.CURVED_ARC_DIRECTION_CENTER]
     * [.CURVED_ARC_DIRECTION_RIGHT]
     */
    fun setCurvedArcDirection(curvedArcDirection: Int) {
        if (mCurvedArcDirection == curvedArcDirection) {
            return
        }
        mCurvedArcDirection = curvedArcDirection
        invalidate()
    }

    /**
     * 获取弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @return 左右圆弧偏移效果方向系数
     */
    fun getCurvedArcDirectionFactor(): Float {
        return mCurvedArcDirectionFactor
    }

    /**
     * 设置弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @param curvedArcDirectionFactor 左右圆弧偏移效果方向系数
     * range 0.0-1.0 越大越明显
     */
    fun setCurvedArcDirectionFactor(@FloatRange(from = 0.0, to = 1.0) curvedArcDirectionFactor: Float) {
        var curvedArcDirectionFactor = curvedArcDirectionFactor
        if (mCurvedArcDirectionFactor == curvedArcDirectionFactor) {
            return
        }
        if (curvedArcDirectionFactor < 0) {
            curvedArcDirectionFactor = 0f
        } else if (curvedArcDirectionFactor > 1) {
            curvedArcDirectionFactor = 1f
        }
        mCurvedArcDirectionFactor = curvedArcDirectionFactor
        invalidate()
    }

    /**
     * 获取折射偏移比例
     *
     * @return 折射偏移比例
     */
    fun getRefractRatio(): Float {
        return mRefractRatio
    }

    /**
     * 设置选中条目折射偏移比例
     *
     * @param refractRatio 折射偏移比例 range 0.0-1.0
     */
    fun setRefractRatio(@FloatRange(from = 0.0, to = 1.0) refractRatio: Float) {
        val tempRefractRatio = mRefractRatio
        mRefractRatio = refractRatio
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO
        }
        if (tempRefractRatio == mRefractRatio) {
            return
        }
        invalidate()
    }

    @Deprecated("")
    fun getCurvedRefractRatio(): Float {
        return mRefractRatio
    }

    @Deprecated("")
    fun setCurvedRefractRatio(@FloatRange(from = 0.0, to = 1.0) refractRatio: Float) {
        setRefractRatio(refractRatio)
    }

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */

    /**
     * WheelView 滚动
     *
     * @param scrollOffsetY 滚动偏移
     */
    protected fun onWheelScroll(scrollOffsetY: Int) {}

    /**
     * WheelView 条目变化
     *
     * @param oldPosition 旧的下标
     * @param newPosition 新下标
     */
    protected fun onWheelItemChanged(oldPosition: Int, newPosition: Int) {}

    /**
     * WheelView 选中
     *
     * @param position 选中的下标
     */
    protected fun onWheelSelected(position: Int) {}

    /**
     * WheelView 滚动状态
     *
     * @param state 滚动状态
     * [WheelView.SCROLL_STATE_IDLE]
     * [WheelView.SCROLL_STATE_DRAGGING]
     * [WheelView.SCROLL_STATE_SCROLLING]
     */
    protected fun onWheelScrollStateChanged(state: Int) {}

    /**
     * 条目选中回调
     *
     * @param data     选中的数据
     * @param position 选中的下标
     */
    protected fun onItemSelected(data: String, position: Int) {}

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */
    /**
     * dp转换px
     *
     * @param dp dp值
     * @return 转换后的px值
     */
    protected fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
    }

    /**
     * sp转换px
     *
     * @param sp sp值
     * @return 转换后的px值
     */
    protected fun sp2px(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)
    }

    /**
     * 条目选中监听器
     *
     * @param <T>
    </T> */
    interface OnItemSelectedListener {
        /**
         * 条目选中回调
         *
         * @param wheelView wheelView
         * @param data      选中的数据
         * @param position  选中的下标
         */
        fun onItemSelected(wheelView: WheelView?, data: String, position: Int)
    }

    /**
     * WheelView滚动状态改变监听器
     */
    interface OnWheelChangedListener {
        /**
         * WheelView 滚动
         *
         * @param scrollOffsetY 滚动偏移
         */
        fun onWheelScroll(scrollOffsetY: Int)

        /**
         * WheelView 条目变化
         *
         * @param oldPosition 旧的下标
         * @param newPosition 新下标
         */
        fun onWheelItemChanged(oldPosition: Int, newPosition: Int)

        /**
         * WheelView 选中
         *
         * @param position 选中的下标
         */
        fun onWheelSelected(position: Int)

        /**
         * WheelView 滚动状态
         *
         * @param state 滚动状态
         * [WheelView.SCROLL_STATE_IDLE]
         * [WheelView.SCROLL_STATE_DRAGGING]
         * [WheelView.SCROLL_STATE_SCROLLING]
         */
        fun onWheelScrollStateChanged(state: Int)
    }

    /**
     * SoundPool 辅助类
     */
    private class SoundHelper private constructor() {
        private var mSoundPool: SoundPool? = null
        private var mSoundId = 0
        /**
         * 获取音量
         *
         * @return 音频播放音量 range 0.0-1.0
         */
        /**
         * 设置音量
         *
         * @param playVolume 音频播放音量 range 0.0-1.0
         */
        var playVolume = 0f

        /**
         * 加载音频资源
         *
         * @param context 上下文
         * @param resId   音频资源 [RawRes]
         */
        fun load(context: Context?, @RawRes resId: Int) {
            if (mSoundPool != null) {
                mSoundId = mSoundPool!!.load(context, resId, 1)
            }
        }

        /**
         * 播放声音效果
         */
        fun playSoundEffect() {
            if (mSoundPool != null && mSoundId != 0) {
                mSoundPool!!.play(mSoundId, playVolume, playVolume, 1, 0, 1f)
            }
        }

        /**
         * 释放SoundPool
         */
        fun release() {
            if (mSoundPool != null) {
                mSoundPool!!.release()
                mSoundPool = null
            }
        }

        companion object {
            /**
             * 初始化 SoundHelper
             *
             * @return SoundHelper 对象
             */
            fun obtain(): SoundHelper {
                return SoundHelper()
            }
        }

        init {
            mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SoundPool.Builder().build()
            } else {
                SoundPool(1, AudioManager.STREAM_SYSTEM, 1)
            }
        }
    }
}