package com.flash.worker.lib.common.base

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.flash.worker.lib.common.module.DataObservable
import com.flash.worker.lib.common.util.Loger
import permission.victor.com.library.OnPermissionCallback
import permission.victor.com.library.PermissionFragmentHelper
import java.util.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BaseFragment
 * Author: Victor
 * Date: 2020/7/3 下午 06:15
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class BaseFragment : Fragment(),Observer, OnPermissionCallback {
    companion object {
        val ID_KEY = "ID_KEY"
        val TAG = javaClass.simpleName
        var fragmentId = -1
    }

    val MULTI_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    protected var rootView: View? = null

    /**
     * 是否初始化过布局
     */
    protected var isViewInitiated = false

    //Fragment对用户可见的标记
    var isVisibleToUser: Boolean = false
    //是否加载过数据
    var isDataInitiated = false

    var permissionFragmentHelper: PermissionFragmentHelper? = null

    protected abstract fun getLayoutResource(): Int
    abstract fun handleBackEvent(): Boolean
    abstract fun freshFragData()

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Loger.e(TAG,"setUserVisibleHint()......isVisibleToUser = ${isVisibleToUser}")
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            this.isVisibleToUser = true
            lazyLoad()
        } else {
            this.isVisibleToUser = false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e(TAG,"onHiddenChanged()......hidden = ${hidden}")
        if (!hidden) {
            this.isVisibleToUser = true
            lazyLoad()
        } else {
            this.isVisibleToUser = false
        }
    }

    fun lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isVisibleToUser && isViewInitiated && !isDataInitiated) {
            freshFragData()
            //数据加载完毕,恢复标记,防止重复加载
            isDataInitiated = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(), container, false)
        }

        if (rootView?.parent != null) {
            val parent = rootView?.parent as ViewGroup
            parent?.removeView(rootView)
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DataObservable.instance.addObserver(this)
        ARouter.getInstance().inject(this)
        permissionFragmentHelper = PermissionFragmentHelper.getInstance(this,this)
        isViewInitiated = true
    }

    fun isPermissionGranted (permissionName: String): Boolean {
        var isPermissionGranted = permissionFragmentHelper?.isPermissionGranted(permissionName) ?: false
        Loger.e(TAG,"isPermissionGranted = $isPermissionGranted")
        return isPermissionGranted
    }

    fun isPermissionDeclined (permissionName: String): Boolean {
        var isPermissionGranted = permissionFragmentHelper?.isPermissionDeclined(permissionName) ?: false
        Loger.e(TAG,"isPermissionDeclined = $isPermissionGranted")
        return isPermissionGranted
    }

    fun isPermissionPermanentlyDenied (permissionName: String): Boolean {
        var isPermissionGranted = permissionFragmentHelper?.isPermissionPermanentlyDenied(permissionName) ?: false
        Loger.e(TAG,"isPermissionDeclined = $isPermissionGranted")
        return isPermissionGranted
    }

    fun requestPermission (permission: String) {
        Loger.e(TAG,"requestPermission-permission = " + permission)
        permissionFragmentHelper?.setForceAccepting(false) // default is false. its here so you know that it exists.
                ?.request(arrayOf(permission))
    }

    fun requestPermission (permissions: Array<String>) {
        Loger.e(TAG,"requestPermission-permissions = " + permissions.toString())
        permissionFragmentHelper?.setForceAccepting(false) // default is false. its here so you know that it exists.
                ?.request(permissions)
    }

    override fun onResume() {
        super.onResume()
        isVisibleToUser = true
        if (!isHidden) {
            lazyLoad()
        }
    }

    override fun onPause() {
        super.onPause()
        isVisibleToUser = false
    }

    override fun onPermissionPreGranted(permissionsName: String) {
        Loger.d(TAG, "onPermissionPreGranted-Permission( " + permissionsName + " ) preGranted")
    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
        Loger.d(TAG, "onPermissionGranted-Permission(s) " + Arrays.toString(permissionName) + " Granted")
    }

    override fun onNoPermissionNeeded() {
        Loger.d(TAG, "onNoPermissionNeeded-Permission(s) not needed")
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        Loger.d(TAG, "ReallyDeclined-Permission " + permissionName + " can only be granted from settingsScreen")
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        Loger.d(TAG, "onPermissionDeclined-Permission(s) " + Arrays.toString(permissionName) + " Declined")
    }

    override fun onPermissionNeedExplanation(p0: String) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionFragmentHelper?.onActivityForResult(requestCode)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionFragmentHelper?.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun update(observable: Observable?, data: Any?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        DataObservable.instance.deleteObserver(this)
        isViewInitiated = false
        isVisibleToUser = false
        isDataInitiated = false
    }


}