package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.EditText
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MNPasswordEditText
 * Author: Victor
 * Date: 2021/2/1 12:02
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class MNPasswordEditText: EditText {
    private val TAG = "MNPasswordEditText"

    private var mContext: Context? = null

    /**
     * 长度
     */
    private var maxLength = 0

    /**
     * 文字的颜色
     */
    private var etTextColor = 0

    /**
     * 文字的画笔
     */
    private var mPaintText: Paint? = null

    /**
     * 线框的画笔
     */
    private var mPaintLine: Paint? = null

    /**
     * 背景色
     */
    private var etBackgroundColor = 0

    /**
     * 线框的颜色
     */
    private var borderColor = 0

    /**
     * 线框被选中的颜色
     */
    private var borderSelectedColor = 0

    /**
     * 线框的圆角
     */
    private var borderRadius = 0f

    /**
     * 线框的宽度
     */
    private var borderWidth = 0f

    /**
     * 密码框的间隔
     */
    private var itemMargin = 0f

    /**
     * 输入的类型
     */
    private var inputMode = 0

    /**
     * 样式
     */
    private var editTextStyle = 0

    /**
     * 文字遮盖
     */
    private var coverText: String? = null

    /**
     * 图片遮盖
     */
    private var coverBitmapID = 0

    /**
     * 图片宽度
     */
    private var coverBitmapWidth = 0f

    /**
     * 圆形遮盖的颜色
     */
    private var coverCirclrColor = 0

    /**
     * 圆形遮盖的半径
     */
    private var coverCirclrRadius = 0f

    /**
     * 线框背景
     */
    private val gradientDrawable = GradientDrawable()
    private var coverBitmap: Bitmap? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        mContext = context

        //初始化参数

        //初始化参数
        initAttrs(attrs, defStyle)

        //初始化相关

        //初始化相关
        init()
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val array = mContext!!.obtainStyledAttributes(attrs, R.styleable.MNPasswordEditText, defStyleAttr, 0)

        //背景色
        etBackgroundColor = array.getColor(R.styleable.MNPasswordEditText_mnPsw_background_color, Color.parseColor("#FFFFFF"))
        //边框颜色
        borderColor = array.getColor(R.styleable.MNPasswordEditText_mnPsw_border_color, Color.parseColor("#FF0000"))
        //边框选中的颜色
        borderSelectedColor = array.getColor(R.styleable.MNPasswordEditText_mnPsw_border_selected_color, 0)
        //文字的颜色
        etTextColor = array.getColor(R.styleable.MNPasswordEditText_mnPsw_text_color, Color.parseColor("#FF0000"))
        //边框圆角
        borderRadius = array.getDimension(R.styleable.MNPasswordEditText_mnPsw_border_radius, dip2px(6f).toFloat())
        //边框线大小
        borderWidth = array.getDimension(R.styleable.MNPasswordEditText_mnPsw_border_width, dip2px(1f).toFloat())
        //每个密码框的间隔
        itemMargin = array.getDimension(R.styleable.MNPasswordEditText_mnPsw_item_margin, dip2px(10f).toFloat())
        //输入的模式
        inputMode = array.getInt(R.styleable.MNPasswordEditText_mnPsw_mode, 1)
        //整体样式
        editTextStyle = array.getInt(R.styleable.MNPasswordEditText_mnPsw_style, 1)
        //替换的图片
        coverBitmapID = array.getResourceId(R.styleable.MNPasswordEditText_mnPsw_cover_bitmap_id, -1)
        //替换的文字
        coverText = array.getString(R.styleable.MNPasswordEditText_mnPsw_cover_text)
        if (TextUtils.isEmpty(coverText)) {
            coverText = "密"
        }
        //圆形的颜色
        coverCirclrColor = array.getColor(R.styleable.MNPasswordEditText_mnPsw_cover_circle_color, Color.parseColor("#FF0000"))
        //密码圆形遮盖半径
        coverCirclrRadius = array.getDimension(R.styleable.MNPasswordEditText_mnPsw_cover_circle_radius, 0f)
        //密码图片遮盖长宽
        coverBitmapWidth = array.getDimension(R.styleable.MNPasswordEditText_mnPsw_cover_bitmap_width, 0f)

        //回收
        array.recycle()
    }

    private fun init() {
        //最大的长度
        maxLength = getMaxLength()
        //隐藏光标
        isCursorVisible = false
        //设置本来文字的颜色为透明
        setTextColor(Color.TRANSPARENT)
        //触摸获取焦点
        isFocusableInTouchMode = true
        //屏蔽长按
        setOnLongClickListener { true }

        //初始化画笔
        //文字
        mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintText?.setStyle(Paint.Style.FILL)
        mPaintText?.setColor(etTextColor)
        mPaintText?.setTextSize(textSize)

        //线
        mPaintLine = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintLine?.setStyle(Paint.Style.STROKE)
        mPaintLine?.setColor(borderColor)
        mPaintLine?.setStrokeWidth(borderWidth)

        //遮盖是图片方式，提前加载图片
        if (inputMode == 2) {
            //判断有没有图片
            coverBitmap = if (coverBitmapID == -1) {
                //抛出异常
                throw NullPointerException("遮盖图片为空")
            } else {
                BitmapFactory.decodeResource(context.resources, coverBitmapID)
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //获取宽高
        val measuredWidth = measuredWidth
        val itemH = measuredHeight.toFloat()
        //方形框
        var margin = itemMargin
        var itemW = (measuredWidth - margin * (maxLength - 1)) / maxLength

        //判断类型
        if (editTextStyle == 1) {
            //连体框
            gradientDrawable.setStroke(borderWidth.toInt(), borderColor)
            gradientDrawable.cornerRadius = borderRadius
            gradientDrawable.setColor(etBackgroundColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //Android系统大于等于API16，使用setBackground
                background = gradientDrawable
            } else {
                //Android系统小于API16，使用setBackgroundDrawable
                setBackgroundDrawable(gradientDrawable)
            }
            margin = 0f
            itemW = measuredWidth / maxLength.toFloat()
            //画线
            for (i in 1 until maxLength) {
                val startX = itemW * i
                val startY = 0f
                canvas.drawLine(startX, startY, startX, itemH, mPaintLine!!)
            }
        } else if (editTextStyle == 2) {
            gradientDrawable.setStroke(borderWidth.toInt(), borderColor)
            gradientDrawable.cornerRadius = borderRadius
            gradientDrawable.setColor(etBackgroundColor)
            val bitmap = drawableToBitmap(gradientDrawable, itemW.toInt(), itemH.toInt())
            var bitmapSelected: Bitmap? = null
            if (borderSelectedColor != 0) {
                gradientDrawable.setStroke(borderWidth.toInt(), borderSelectedColor)
                bitmapSelected = drawableToBitmap(gradientDrawable, itemW.toInt(), itemH.toInt())
            }
            //画每个Item背景
            for (i in 0 until maxLength) {
                val left = itemW * i + margin * i
                val top = 0f
                if (bitmapSelected == null) {
                    canvas.drawBitmap(bitmap, left, top, mPaintLine)
                } else {
                    if (text.length == i) {
                        //选中是另外的颜色
                        canvas.drawBitmap(bitmapSelected, left, top, mPaintLine)
                    } else {
                        canvas.drawBitmap(bitmap, left, top, mPaintLine)
                    }
                }
            }
        } else if (editTextStyle == 3) {
            //下划线格式
            for (i in 0 until maxLength) {
                if (borderSelectedColor != 0) {
                    if (text.length == i) {
                        //选中是另外的颜色
                        mPaintLine!!.color = borderSelectedColor
                    } else {
                        mPaintLine!!.color = borderColor
                    }
                } else {
                    mPaintLine!!.color = borderColor
                }
                val startX = itemW * i + itemMargin * i
                val startY = itemH - borderWidth
                val stopX = startX + itemW
                canvas.drawLine(startX, startY, stopX, startY, mPaintLine!!)
            }
        }

        //写文字
        val currentText = text.toString()
        for (i in 0 until maxLength) {
            if (!TextUtils.isEmpty(currentText) && i < currentText.length) {
                //<!--密码框输入的模式:1.圆形，2.图片，3.文字，4.明文-->
                if (inputMode == 1) {
                    //圆点半径
                    var circleRadius = itemW * 0.5f * 0.5f
                    if (circleRadius > itemH / 2f) {
                        circleRadius = itemH * 0.5f * 0.5f
                    }
                    if (coverCirclrRadius > 0) {
                        circleRadius = coverCirclrRadius
                    }
                    val startX = itemW / 2f + itemW * i + margin * i
                    val startY = itemH / 2.0f
                    mPaintText!!.color = coverCirclrColor
                    canvas.drawCircle(startX, startY, circleRadius, mPaintText!!)
                } else if (inputMode == 2) {
                    var picW = itemW * 0.5f
                    if (coverBitmapWidth > 0) {
                        picW = coverBitmapWidth
                    }
                    val startX = (itemW - picW) / 2f + itemW * i + margin * i
                    val startY = (itemH - picW) / 2f
                    val bitmap = Bitmap.createScaledBitmap(coverBitmap!!, picW.toInt(), picW.toInt(), true)
                    canvas.drawBitmap(bitmap, startX, startY, mPaintText)
                } else if (inputMode == 3) {
                    val fontWidth = getFontWidth(mPaintText!!, coverText!!)
                    val fontHeight = getFontHeight(mPaintText!!, coverText!!)
                    val startX = (itemW - fontWidth) / 2f + itemW * i + margin * i
                    val startY = (itemH + fontHeight) / 2f - 6
                    mPaintText!!.color = etTextColor
                    canvas.drawText(coverText!!, startX, startY, mPaintText!!)
                } else {
                    val StrPosition = currentText[i].toString()
                    val fontWidth = getFontWidth(mPaintText!!, StrPosition)
                    val fontHeight = getFontHeight(mPaintText!!, StrPosition)
                    val startX = (itemW - fontWidth) / 2f + itemW * i + margin * i
                    val startY = (itemH + fontHeight) / 2f
                    mPaintText!!.color = etTextColor
                    canvas.drawText(StrPosition, startX, startY, mPaintText!!)
                }
            }
        }
    }

    fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
                width,
                height,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }


    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        //刷新界面
        invalidate()
        if (onTextChangeListener != null) {
            if (getText().toString().length == getMaxLength()) {
                onTextChangeListener!!.onTextChange(getText().toString(), true)
            } else {
                onTextChangeListener!!.onTextChange(getText().toString(), false)
            }
        }
    }

    fun getFontWidth(paint: Paint, str: String): Float {
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        return rect.width().toFloat()
    }

    fun getFontHeight(paint: Paint, str: String): Float {
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        return rect.height().toFloat()
    }

    fun getMaxLength(): Int {
        var length = 0
        try {
            val inputFilters = filters
            for (filter in inputFilters) {
                val c: Class<*> = filter.javaClass
                if (c.name == "android.text.InputFilter\$LengthFilter") {
                    val f = c.declaredFields
                    for (field in f) {
                        if (field.name == "mMax") {
                            field.isAccessible = true
                            length = field[filter] as Int
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return length
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private var onTextChangeListener: OnTextChangeListener? = null

    fun setOnTextChangeListener(onTextChangeListener: OnTextChangeListener?) {
        this.onTextChangeListener = onTextChangeListener
    }

    interface OnTextChangeListener {
        /**
         * 监听输入变化
         *
         * @param text       当前的文案
         * @param isComplete 是不是完成输入
         */
        fun onTextChange(text: String?, isComplete: Boolean)
    }

}