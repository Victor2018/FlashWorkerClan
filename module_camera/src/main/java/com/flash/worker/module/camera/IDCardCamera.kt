package com.flash.worker.module.camera

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.data.IDCardType
import com.flash.worker.module.camera.view.activity.CameraActivity
import java.lang.ref.WeakReference


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IDCardCamera
 * Author: Victor
 * Date: 2020/12/18 15:31
 * Description: 
 * -----------------------------------------------------------------
 */
class IDCardCamera {
    companion object {
        const val RESULT_CODE = 0X11 //结果码

        const val PERMISSION_CODE_FIRST = 0x12 //权限请求码

        const val IMAGE_PATH = "image_path" //图片路径标记
    }

    private var mActivity: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null

    constructor(activity: Activity) {
        mActivity = WeakReference(activity)
    }
    constructor(fragment: Fragment) {
        mFragment = WeakReference(fragment)
    }


    fun create (activity: Activity): IDCardCamera {
        return IDCardCamera(activity)
    }

    fun create (fragment: Fragment): IDCardCamera {
        return IDCardCamera(fragment)
    }

    /**
     * 打开相机
     *
     * @param IDCardDirection 身份证方向（TYPE_IDCARD_FRONT / TYPE_IDCARD_BACK）
     */
    fun openCamera(IDCardDirection: Int) {
        val activity = mActivity!!.get()
        val fragment = mFragment!!.get()
        val intent =
            Intent(activity, CameraActivity::class.java)
        intent.putExtra(IDCardType.TAKE_TYPE, IDCardDirection)
        if (fragment != null) {
            fragment.startActivityForResult(intent, IDCardDirection)
        } else {
            activity?.startActivityForResult(intent, IDCardDirection)
        }
    }

    /**
     * 获取图片路径
     *
     * @param data Intent
     * @return 图片路径
     */
    fun getImagePath(data: Intent?): String? {
        return if (data != null) {
            data.getStringExtra(IMAGE_PATH)
        } else ""
    }
}