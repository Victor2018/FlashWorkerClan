package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.flash.worker.lib.common.R
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShapedImageView
 * Author: Victor
 * Date: 2020/12/15 17:15
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class ShapedImageView: ImageView {

    companion object {
        const val SHAPE_MODE_ROUND_RECT = 1
        const val SHAPE_MODE_CIRCLE = 2
    }

    var mShapeMode = SHAPE_MODE_ROUND_RECT
    private var mRadius = 0f
    private var mStrokeColor = 0x26000000
    private var mStrokeWidth = 0f
    private var mShapeChanged = false

    private var mPath: Path? = null
    private var mShape: Shape? = null
    private  var mStrokeShape:Shape? = null
    private var mPaint: Paint? = null
    private  var mStrokePaint:Paint? = null
    private  var mPathPaint:Paint? = null
    private var mShapeBitmap: Bitmap? = null
    private  var mStrokeBitmap:Bitmap? = null

    private var mExtension: PathExtension? = null

    private val DST_IN: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private val DST_OUT: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(context,attrs)
    }

    private fun initAttr(context: Context,attrs: AttributeSet?) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        if (attrs != null) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ShapedImageView)
            mShapeMode =
                a.getInt(R.styleable.ShapedImageView_shape_mode, SHAPE_MODE_ROUND_RECT)
            mRadius = a.getDimension(R.styleable.ShapedImageView_round_radius, 0f)
            mStrokeWidth = a.getDimension(R.styleable.ShapedImageView_stroke_width, 0f)
            mStrokeColor =
                a.getColor(R.styleable.ShapedImageView_stroke_color, mStrokeColor)
            a.recycle()
        }
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.setFilterBitmap(true)
        mPaint?.setColor(Color.BLACK)
        mPaint?.setXfermode(DST_IN)
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint!!.isFilterBitmap = true
        mStrokePaint!!.color = Color.BLACK
        mPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPathPaint!!.isFilterBitmap = true
        mPathPaint!!.color = Color.BLACK
        mPathPaint!!.xfermode = DST_OUT
        mPath = Path()
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed || mShapeChanged) {
            mShapeChanged = false
            val width = measuredWidth
            val height = measuredHeight
            when (mShapeMode) {
                SHAPE_MODE_ROUND_RECT -> {
                }
                SHAPE_MODE_CIRCLE -> {
                    val min = Math.min(width, height)
                    mRadius = min.toFloat() / 2
                }
            }
            if (mShape == null || mRadius != 0f) {
                val radius = FloatArray(8)
                Arrays.fill(radius, mRadius)
                mShape = RoundRectShape(radius, null, null)
                mStrokeShape = RoundRectShape(radius, null, null)
            }
            mShape!!.resize(width.toFloat(), height.toFloat())
            mStrokeShape!!.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2)
            makeStrokeBitmap()
            makeShapeBitmap()
            if (mExtension != null) {
                mExtension?.onLayout(mPath, width, height)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val saveLayers = canvas.saveLayer(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            null,
            Canvas.ALL_SAVE_FLAG
        )
        try {
            super.onDraw(canvas)
        } catch (throwable: Throwable) {
        }
        if (mStrokeWidth > 0 && mStrokeShape != null) {
            if (mStrokeBitmap == null || mStrokeBitmap!!.isRecycled) {
                makeStrokeBitmap()
            }
            val i = canvas.saveLayer(
                0f,
                0f,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                null,
                Canvas.ALL_SAVE_FLAG
            )
            mStrokePaint!!.xfermode = null
            if (mStrokeBitmap != null && !mStrokeBitmap!!.isRecycled) {
                canvas.drawBitmap(mStrokeBitmap!!, 0f, 0f, mStrokePaint)
            }
            canvas.translate(mStrokeWidth, mStrokeWidth)
            mStrokePaint!!.xfermode = DST_OUT
            mStrokeShape!!.draw(canvas, mStrokePaint)
            canvas.restoreToCount(i)
        }
        if (mExtension != null) {
            canvas.drawPath(mPath!!, mPathPaint!!)
        }
        when (mShapeMode) {
            SHAPE_MODE_ROUND_RECT, SHAPE_MODE_CIRCLE -> {
                if (mShapeBitmap == null || mShapeBitmap?.isRecycled()!!) {
                    makeShapeBitmap()
                }
                if (mShapeBitmap != null && !mShapeBitmap!!.isRecycled()) {
                    canvas.drawBitmap(mShapeBitmap!!, 0f, 0f, mPaint)
                }
            }
        }
        canvas.restoreToCount(saveLayers)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseBitmap(mShapeBitmap)
        releaseBitmap(mStrokeBitmap)
    }

    private fun makeStrokeBitmap() {
        if (mStrokeWidth <= 0) return
        val w = measuredWidth
        val h = measuredHeight
        if (w == 0 || h == 0) return
        releaseBitmap(mStrokeBitmap)
        mStrokeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(mStrokeBitmap!!)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = mStrokeColor
        c.drawRect(RectF(0f, 0f, w.toFloat(), h.toFloat()), p)
    }

    private fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    private fun makeShapeBitmap() {
        val w = measuredWidth
        val h = measuredHeight
        if (w == 0 || h == 0) return
        releaseBitmap(mShapeBitmap)
        mShapeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(mShapeBitmap!!)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.RED
        mShape!!.draw(c, p)
    }

    fun setExtension(extension: PathExtension) {
        mExtension = extension
        requestLayout()
    }

    fun setStroke(strokeColor: Int, strokeWidth: Float) {
        if (mStrokeWidth <= 0) return
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth
            val width = measuredWidth
            val height = measuredHeight
            mStrokeShape!!.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2)
            postInvalidate()
        }
        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor
            makeStrokeBitmap()
            postInvalidate()
        }
    }

    fun setStrokeColor(strokeColor: Int) {
        setStroke(strokeColor, mStrokeWidth)
    }

    fun setStrokeWidth(strokeWidth: Float) {
        setStroke(mStrokeColor, strokeWidth)
    }

    fun setShape(shapeMode: Int, radius: Float) {
        mShapeChanged = mShapeMode != shapeMode || mRadius != radius
        if (mShapeChanged) {
            mShapeMode = shapeMode
            mRadius = radius
            mShape = null
            mStrokeShape = null
            requestLayout()
        }
    }

    fun setShapeMode(shapeMode: Int) {
        setShape(shapeMode, mRadius)
    }

    fun setShapeRadius(radius: Float) {
        setShape(mShapeMode, radius)
    }

    interface PathExtension {
        fun onLayout(path: Path?, width: Int, height: Int)
    }
}