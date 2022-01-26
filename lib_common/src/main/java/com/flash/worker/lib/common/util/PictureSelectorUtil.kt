package com.flash.worker.lib.common.util

import android.app.Activity
import android.os.Environment
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.module.GlideEngine
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PictureSelectorUtil
 * Author: Victor
 * Date: 2020/12/1 9:34
 * Description: 
 * -----------------------------------------------------------------
 */
object PictureSelectorUtil {
    private const val TAG = "PictureSelectorUtil"
    fun selectMedia(activity: Activity?, isSelectVideo: Boolean, crop: Boolean,isAvatar: Boolean, maxSelectCount: Int) {
        var maxSelectCount = maxSelectCount
        val pictureSelector: PictureSelector? = getPictureSelector(activity, null)
        val singleSelect = maxSelectCount == 1
        if (singleSelect) {
            maxSelectCount = 2
        }
        configPictureSelector(pictureSelector, isSelectVideo, singleSelect, crop,isAvatar, maxSelectCount)
    }

    fun selectMedia(fragment: Fragment?, isSelectVideo: Boolean, crop: Boolean,isAvatar: Boolean, maxSelectCount: Int) {
        var maxSelectCount = maxSelectCount
        val pictureSelector: PictureSelector? = getPictureSelector(null, fragment)
        val singleSelect = maxSelectCount == 1
        if (singleSelect) {
            maxSelectCount = 2
        }
        configPictureSelector(pictureSelector, isSelectVideo, singleSelect, crop,isAvatar, maxSelectCount)
    }

    fun openCamera(activity: Activity?, isSelectVideo: Boolean,crop: Boolean) {
        val pictureSelector: PictureSelector? = getPictureSelector(activity, null)
        configCamera(pictureSelector,isSelectVideo,true,crop,1)
    }
    fun openCamera(fragment: Fragment?, isSelectVideo: Boolean,crop: Boolean) {
        val pictureSelector: PictureSelector? = getPictureSelector(null, fragment)
        configCamera(pictureSelector,isSelectVideo,true,crop,1)
    }

    private fun getPictureSelector(activity: Activity?, fragment: Fragment?): PictureSelector? {
        // 进入相册 以下是例子：不需要的api可以不写
        var pictureSelector: PictureSelector? = null
        if (activity != null) {
            pictureSelector = PictureSelector.create(activity)
        }
        if (fragment != null) {
            pictureSelector = PictureSelector.create(fragment)
        }
        return pictureSelector
    }

    private fun configPictureSelector(pictureSelector: PictureSelector?,
                                      isSelectVideo: Boolean, singleSelect: Boolean,
                                      crop: Boolean, isAvatar: Boolean,maxSelectCount: Int) {
        if (pictureSelector == null) return
        pictureSelector.openGallery(if (isSelectVideo) PictureMimeType.ofVideo() else PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .loadImageEngine(GlideEngine.instance) // 请参考Demo GlideEngine.java
                //                .queryMaxFileSize(10)//只查多少M以内的图片、视频、音频  单位M
                .maxSelectNum(maxSelectCount) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .imageSpanCount(4) // 每行显示个数
                .selectionMode(if (singleSelect) PictureConfig.SINGLE else PictureConfig.MULTIPLE) // 多选 or 单选
                .previewImage(true) // 是否可预览图片
                .isCamera(true) // 是否显示拍照按钮
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                .enableCrop(crop) // 是否裁剪
                .withAspectRatio(if (isAvatar) 1 else 0, if (isAvatar) 1 else 0)
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .compress(true) // 是否压缩
                .minimumCompressSize(800) // 小于800KB的图片不压缩
                .compressQuality(80)
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                .glideOverride(160, 160) // glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .videoMinSecond(2) //显示至少2s的视频
                .videoMaxSecond(15) //显示15秒以内的视频
                //                .imageMinWidth(500)
                //                .imageMinHeight(500)
                .forResult(PictureConfig.CHOOSE_REQUEST) //结果回调onActivityResult code
    }
    private fun configCamera(pictureSelector: PictureSelector?,
                                      isSelectVideo: Boolean, singleSelect: Boolean,
                                      crop: Boolean, maxSelectCount: Int) {
        if (pictureSelector == null) return
        pictureSelector.openCamera(if (isSelectVideo) PictureMimeType.ofVideo() else PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .loadImageEngine(GlideEngine.instance) // 请参考Demo GlideEngine.java
                //                .queryMaxFileSize(10)//只查多少M以内的图片、视频、音频  单位M
                .maxSelectNum(maxSelectCount) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .imageSpanCount(4) // 每行显示个数
                .selectionMode(if (singleSelect) PictureConfig.SINGLE else PictureConfig.MULTIPLE) // 多选 or 单选
                .previewImage(true) // 是否可预览图片
                .isCamera(true) // 是否显示拍照按钮
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                .enableCrop(crop) // 是否裁剪
                .withAspectRatio(1, 1)
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .compress(true) // 是否压缩
                .minimumCompressSize(800) // 小于800KB的图片不压缩
                .compressQuality(80)
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                .glideOverride(160, 160) // glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .videoMinSecond(2) //显示至少2s的视频
                .videoMaxSecond(15) //显示15秒以内的视频
                //                .imageMinWidth(500)
                //                .imageMinHeight(500)
                .forResult(PictureConfig.CHOOSE_REQUEST) //结果回调onActivityResult code
    }

    fun getPath(): String? {
        val path = Environment.getExternalStorageState().toString() + "/Luban/image/"
        val file = File(path)
        return if (file.mkdirs()) {
            path
        } else path
    }

    fun isVideo(media: LocalMedia?): Boolean {
        var isSelectVideo = false
        if (media == null) return isSelectVideo
        isSelectVideo = PictureMimeType.isHasVideo(media.getMimeType())
        Loger.e(TAG, "isVideo()-isSelectVideo = $isSelectVideo")
        return isSelectVideo
    }
}