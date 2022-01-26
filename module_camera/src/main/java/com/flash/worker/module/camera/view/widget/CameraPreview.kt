package com.flash.worker.module.camera.view.widget

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.module.camera.AutoFocusManager
import com.flash.worker.module.camera.SensorControler
import com.flash.worker.module.camera.util.CameraUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CameraPreview
 * Author: Victor
 * Date: 2020/12/18 15:41
 * Description: 
 * -----------------------------------------------------------------
 */
class CameraPreview: SurfaceView, SurfaceHolder.Callback {
    val TAG = "CameraPreview"
    private var camera: Camera? = null
    private var mAutoFocusManager: AutoFocusManager? = null
    private var mSensorControler: SensorControler? = null
    private var mContext: Context? = null
    private var mSurfaceHolder: SurfaceHolder? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        initAttr(context)
    }

    private fun initAttr(context: Context) {
        mContext = context
        mSurfaceHolder = holder
        mSurfaceHolder?.addCallback(this)
        mSurfaceHolder?.setKeepScreenOn(true)
        mSurfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mSensorControler =
            SensorControler.getInstance(
                context.applicationContext
            )
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //因为设置了固定屏幕方向，所以在实际使用中不会触发这个方法
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        holder!!.removeCallback(this)
        //回收释放资源
        //回收释放资源
        release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera = CameraUtils.openCamera()
        if (camera != null) {
            try {
                camera?.setPreviewDisplay(holder)
                val parameters = camera?.getParameters()
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                    camera?.setDisplayOrientation(90)
                    parameters?.setRotation(90)
                } else {
                    camera?.setDisplayOrientation(0)
                    parameters?.setRotation(0)
                }
                val sizeList =
                    parameters?.supportedPreviewSizes //获取所有支持的预览大小
                val bestSize: Camera.Size = getOptimalPreviewSize(sizeList,
                    DensityUtil.getDisplayWidth(),
                    DensityUtil.getDisplayHeight()
                )!!
                parameters?.setPreviewSize(bestSize.width, bestSize.height) //设置预览大小
                camera?.setParameters(parameters)
                camera?.startPreview()
                focus() //首次对焦
                //mAutoFocusManager = new AutoFocusManager(camera);//定时对焦
            } catch (e: Exception) {
                Log.d(TAG, "Error setting camera preview: " + e.message)
                try {
                    val parameters = camera?.getParameters()
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                        camera?.setDisplayOrientation(90)
                        parameters?.setRotation(90)
                    } else {
                        camera?.setDisplayOrientation(0)
                        parameters?.setRotation(0)
                    }
                    camera?.setParameters(parameters)
                    camera?.startPreview()
                    focus() //首次对焦
                    //mAutoFocusManager = new AutoFocusManager(camera);//定时对焦
                } catch (e1: Exception) {
                    e.printStackTrace()
                    camera = null
                }
            }
        }
    }


    /**
     * 获取最佳预览大小
     *
     * @param sizes 所有支持的预览大小
     * @param w     SurfaceView宽
     * @param h     SurfaceView高
     * @return
     */
    fun getOptimalPreviewSize(
        sizes: List<Camera.Size>?,
        w: Int,
        h: Int
    ): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = w.toDouble() / h
        if (sizes == null) return null
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        // Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    /**
     * 释放资源
     */
    private fun release() {
        camera?.setPreviewCallback(null)
        camera?.stopPreview()
        camera?.release()
        camera = null
        if (mAutoFocusManager != null) {
            mAutoFocusManager?.stop()
            mAutoFocusManager = null
        }
    }

    /**
     * 对焦，在CameraActivity中触摸对焦或者自动对焦
     */
    fun focus() {
        try {
            camera?.autoFocus(null)
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "takePhoto $e")
        }
    }

    /**
     * 开关闪光灯
     *
     * @return 闪光灯是否开启
     */
    fun switchFlashLight(): Boolean {
        val parameters = camera?.parameters
        return if (parameters?.flashMode == Camera.Parameters.FLASH_MODE_OFF) {
            parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            camera?.parameters = parameters
            true
        } else {
            parameters?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera?.parameters = parameters
            false
        }
        return false
    }

    /**
     * 拍摄照片
     *
     * @param pictureCallback 在pictureCallback处理拍照回调
     */
    fun takePhoto(pictureCallback: Camera.PictureCallback?) {
        try {
            camera?.takePicture(null, null, pictureCallback)
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "takePhoto $e")
        }
    }

    fun startPreview() {
        camera?.startPreview()
    }

    fun onStart() {
        addCallback()
        mSensorControler?.onStart()
        mSensorControler?.mCameraFocusListener = (object :
            SensorControler.CameraFocusListener {
            override fun onFocus() {
                focus()
            }
        })
    }

    fun onStop() {
        mSensorControler?.onStop()
    }

    fun addCallback() {
        mSurfaceHolder?.addCallback(this)
    }

}