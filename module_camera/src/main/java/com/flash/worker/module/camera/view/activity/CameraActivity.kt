package com.flash.worker.module.camera.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.IDCardType
import com.flash.worker.lib.common.util.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.flash.worker.lib.livedatabus.action.CameraActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.camera.util.CameraUtils
import com.flash.worker.module.camera.R
import com.flash.worker.module.camera.cropper.CropListener
import com.flash.worker.module.camera.util.FastClickUtil
import com.flash.worker.module.camera.util.PermissionUtil
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CameraActivity
 * Author: Victor
 * Date: 2020/12/18 16:41
 * Description: 
 * -----------------------------------------------------------------
 */

@Route(path = ARouterPath.CameraAct)
class CameraActivity: Activity(),View.OnClickListener {

    private val ACCESS_CAMERA_CODE = 0x301
    private val ACCESS_WRITE_EXTERNAL_STORAGE_CODE = 0x302

    private var mCropBitmap: Bitmap? = null
    private var mType = IDCardType.TYPE_IDCARD_FRONT//拍摄类型

    var selectList: MutableList<LocalMedia> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        initialize()
    }

    private fun initialize() {
        if (!PermissionUtil.hasPermission(this,Manifest.permission.CAMERA)) {
            PermissionUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA),
                ACCESS_CAMERA_CODE)
            return
        }
        if (!PermissionUtil.hasPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestPermission(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),ACCESS_WRITE_EXTERNAL_STORAGE_CODE)
            return
        }
        mType = intent.getIntExtra(IDCardType.TAKE_TYPE, IDCardType.TYPE_IDCARD_FRONT)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val screenMinSize: Float =
            Math.min(DensityUtil.getDisplayWidth(), DensityUtil.getDisplayHeight()).toFloat()
        val screenMaxSize: Float =
            Math.max(DensityUtil.getDisplayWidth(), DensityUtil.getDisplayHeight()).toFloat()
        val height: Float = (screenMinSize * 0.75).toFloat()
        val width: Float = height * 75.0f / 47.0f
        //获取底部"操作区域"的宽度
        val flCameraOptionWidth = (screenMaxSize - width) / 2
        val containerParams =
            LinearLayout.LayoutParams(
                width.toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        val cropParams =
            LinearLayout.LayoutParams(width.toInt(), height.toInt())
        val cameraOptionParams =
            LinearLayout.LayoutParams(
                flCameraOptionWidth.toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        mLlCameraCropContainer.setLayoutParams(containerParams)
        mIvCameraCrop.setLayoutParams(cropParams)
        //获取"相机裁剪区域"的宽度来动态设置底部"操作区域"的宽度，使"相机裁剪区域"居中
        mFlCameraOption.setLayoutParams(cameraOptionParams)

        when (mType) {
            IDCardType.TYPE_IDCARD_FRONT -> mIvCameraCrop.setImageResource(R.mipmap.camera_idcard_front)
            IDCardType.TYPE_IDCARD_BACK -> mIvCameraCrop.setImageResource(R.mipmap.camera_idcard_back)
        }

        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/Handler()
            .postDelayed({ runOnUiThread { mCameraPreview.visibility = View.VISIBLE } }, 500)

        initListener()
    }

    private fun initListener() {
        mCameraPreview.setOnClickListener(this)
        mIvCameraFlash.setOnClickListener(this)
        mIvCameraClose.setOnClickListener(this)
        mIvCameraTake.setOnClickListener(this)
        mIvCameraResultOk.setOnClickListener(this)
        mIvCameraResultCancel.setOnClickListener(this)
        mTvAlbum.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mCameraPreview -> {
                mCameraPreview.focus()
            }
            mIvCameraFlash -> {
                if (CameraUtils.hasFlash(this)) {
                    val isFlashOn = mCameraPreview.switchFlashLight()
                    mIvCameraFlash.setImageResource(if (isFlashOn) R.mipmap.camera_flash_on
                    else R.mipmap.camera_flash_off)
                } else {
                    ToastUtils.show(R.string.camera_no_flash)
                }
            }
            mIvCameraClose -> {
                finish()
            }
            mIvCameraTake -> {
                if (!FastClickUtil.isFastClick()) {
                    takePhoto()
                }
            }
            mIvCameraResultOk -> {
                confirm()
            }
            mIvCameraResultCancel -> {
                mCameraPreview.isEnabled = true
                mCameraPreview.addCallback()
                mCameraPreview.startPreview()
                mIvCameraFlash.setImageResource(R.mipmap.camera_flash_off)
                setTakePhotoLayout()
            }
            mTvAlbum -> {
                PictureSelectorUtil.selectMedia(this,false,false,
                    false,1)
            }
        }
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        mCameraPreview.isEnabled = false
        CameraUtils.getCamera()
            ?.setOneShotPreviewCallback(Camera.PreviewCallback { bytes, camera ->
                val size = camera.parameters.previewSize //获取预览大小
                camera.stopPreview()
                Thread(Runnable {
                    val w = size.width
                    val h = size.height
                    val bitmap: Bitmap = BitmapUtil.getBitmapFromByte(bytes, w, h)!!
                    cropImage(bitmap,false)
                }).start()
            })
    }

    /**
     * 裁剪图片
     */
    private fun cropImage(bitmap: Bitmap,crop: Boolean) {
        /*计算扫描框的坐标点*/
        val left = mViewCameraCropLeft.width.toFloat()
        val top = mIvCameraCrop.top.toFloat()
        val right = mIvCameraCrop.right + left
        val bottom = mIvCameraCrop.bottom.toFloat()

        /*计算扫描框坐标点占原图坐标点的比例*/
        val leftProportion = left / mCameraPreview.width
        val topProportion = top / mCameraPreview.height
        val rightProportion = right / mCameraPreview.width
        val bottomProportion = bottom / mCameraPreview.bottom

        if (crop) {
            /*自动裁剪*/
            mCropBitmap = Bitmap.createBitmap(
                    bitmap,
                    (leftProportion * bitmap.width.toFloat()).toInt(),
                    (topProportion * bitmap.height.toFloat()).toInt(),
                    ((rightProportion - leftProportion) * bitmap.width.toFloat()).toInt(),
                    ((bottomProportion - topProportion) * bitmap.height.toFloat()).toInt()
            )
        } else {
            mCropBitmap = bitmap
        }

        /*设置成手动裁剪模式*/runOnUiThread { //将手动裁剪区域设置成与扫描框一样大
            mCropImageView.layoutParams = LinearLayout.LayoutParams(
                mIvCameraCrop.width,
                mIvCameraCrop.height
            )
            setCropLayout()
            mCropImageView.setImageBitmap(mCropBitmap)
        }
    }

    /**
     * 设置裁剪布局
     */
    private fun setCropLayout() {
        mIvCameraCrop.visibility = View.GONE
        mCameraPreview.visibility = View.GONE
        mLlCameraOption.visibility = View.GONE
        mTvAlbum.visibility = View.GONE
        mCropImageView.visibility = View.VISIBLE
        mLlCameraResult.visibility = View.VISIBLE
        mViewCameraCropBottom.text = ""
    }

    /**
     * 设置拍照布局
     */
    private fun setTakePhotoLayout() {
        mIvCameraCrop.visibility = View.VISIBLE
        mCameraPreview.visibility = View.VISIBLE
        mLlCameraOption.visibility = View.VISIBLE
        mTvAlbum.visibility = View.VISIBLE
        mCropImageView.visibility = View.GONE
        mLlCameraResult.visibility = View.GONE
        mViewCameraCropBottom.text = getString(R.string.camera_touch_to_focus)
        mCameraPreview.focus()
    }

    /**
     * 点击确认，返回图片路径
     */
    private fun confirm() {
        /*手动裁剪图片*/
        mCropImageView.crop(object : CropListener {
            override fun onFinish(bitmap: Bitmap?) {
                if (bitmap == null) {
                    ToastUtils.show(R.string.camera_crop_fail)
                    finish()
                }

                /*保存图片到sdcard并返回图片路径*/
                val imagePath: String =
                    StringBuffer().append(FileUtil.getImageCacheDir(this@CameraActivity))
                        .append(File.separator)
                        .append(System.currentTimeMillis()).append(".jpg").toString()
                if (FileUtil.save(
                        bitmap,
                        imagePath,
                        Bitmap.CompressFormat.JPEG
                    )
                ) {
//                    val intent = Intent()
//                    intent.putExtra(IDCardCamera.IMAGE_PATH, imagePath)
//                    setResult(IDCardCamera.RESULT_CODE, intent)

                    if (mType == IDCardType.TYPE_IDCARD_FRONT) {
                        LiveDataBus.send(CameraActions.IDCARD_FRONT_RESULT,imagePath)
                    }
                    if (mType == IDCardType.TYPE_IDCARD_BACK) {
                        LiveDataBus.send(CameraActions.IDCARD_BACK_RESULT,imagePath)
                    }
                    finish()
                }
            }
        }, true)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_CAMERA_CODE) {
            initialize()
        } else if (requestCode == ACCESS_WRITE_EXTERNAL_STORAGE_CODE) {
            initialize()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val list = PictureSelector.obtainMultipleResult(data)
                    Loger.e(BaseFragment.TAG,"onActivityResult-res = " + JsonUtils.toJSONString(list.get(0)))
                    selectList.clear()
                    selectList.addAll(list)

                    var path = selectList.get(0).path
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        path = selectList.get(0).androidQToPath
                    }

                    var imageBitmap = BitmapFactory.decodeFile(path)
                    Loger.e("CameraActivity","imageBitmap.width = " + imageBitmap.width)
                    Loger.e("CameraActivity","imageBitmap.height = " + imageBitmap.height)
                    cropImage(imageBitmap,false)

                    PictureSelector.obtainMultipleResult(data).clear()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mCameraPreview != null) {
            mCameraPreview.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mCameraPreview != null) {
            mCameraPreview.onStop()
        }
    }
}