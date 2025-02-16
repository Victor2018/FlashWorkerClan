package com.flash.worker.lib.common.base

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.module.DataObservable
import com.flash.worker.lib.common.util.*
import permission.victor.com.library.OnPermissionCallback
import permission.victor.com.library.PermissionHelper
import java.util.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BaseActivity
 * Author: Victor
 * Date: 2020/7/3 下午 06:17
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class BaseActivity: AppCompatActivity(), OnPermissionCallback,Observer,
    MainHandler.OnMainHandlerImpl {
    protected var TAG = javaClass.simpleName
    var statusBarTextColorBlack: Boolean = true
    var setNavigationBarBottomPading: Boolean = true
    var permissionHelper: PermissionHelper? = null
    private var neededPermission: Array<String>? = null
    private var builder: AlertDialog? = null

    val MULTI_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    abstract fun getLayoutResource(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(getLayoutResource())
        initializeSuper()
    }

    fun initializeSuper () {
        Log.e(TAG,"initializeSuper......")

        DataObservable.instance.addObserver(this)
        //状态栏背景及字体颜色适配
        StatusBarUtil.translucentStatusBar(this, true,statusBarTextColorBlack,false)

        permissionHelper = PermissionHelper.getInstance(this,this)

    }

    fun isPermissionGranted (permissionName: String): Boolean {
        var isPermissionGranted = permissionHelper?.isPermissionGranted(permissionName) ?: false
        Loger.e(TAG,"isPermissionGranted = $isPermissionGranted")
        return isPermissionGranted
    }

    fun requestPermission (permission: String) {
        Loger.e(TAG,"requestPermission-permission = " + permission)
        permissionHelper?.setForceAccepting(false) // default is false. its here so you know that it exists.
            ?.request(arrayOf(permission))
    }
    fun requestPermission (permissions: Array<String>) {
        Loger.e(TAG,"requestPermission-permissions = " + permissions.toString())
        permissionHelper?.setForceAccepting(false) // default is false. its here so you know that it exists.
            ?.request(permissions)
    }

    override fun onDestroy() {
        super.onDestroy()
        DataObservable.instance.deleteObserver(this)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_activity_enter_back, R.anim.anim_activity_exit_back)
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
        neededPermission = PermissionHelper.declinedPermissions(this, MULTI_PERMISSIONS)
        val builder = StringBuilder(neededPermission!!.size)
        if (neededPermission!!.size > 0) {
            for (permission in neededPermission!!) {
                builder.append(permission).append("\n")
            }
        }
        val alert = getAlertDialog(neededPermission!!, builder.toString())
        if (!alert.isShowing) {
            alert.show()
        }
    }

    fun getAlertDialog(permissions: Array<String>, permissionName: String): AlertDialog {
        if (builder == null) {
            builder = AlertDialog.Builder(this)
                .setTitle("Permission Needs Explanation")
                .create()
        }
        builder?.setButton(DialogInterface.BUTTON_POSITIVE, "Request", DialogInterface.OnClickListener { dialog, which -> permissionHelper?.requestAfterExplanation(permissions) })
        builder?.setMessage("Permissions need explanation ($permissionName)")
        return builder!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHelper?.onActivityForResult(requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun update(observable: Observable?, data: Any?) {

    }

    override fun handleMainMessage(message: Message?) {

    }

}