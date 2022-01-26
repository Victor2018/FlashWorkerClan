package com.flash.worker.lib.common.module

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.util.AmapLocationUtil
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.Loger
import permission.victor.com.library.PermissionHelper
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LocationHelper
 * Author: Victor
 * Date: 2021/1/5 19:50
 * Description: 
 * -----------------------------------------------------------------
 */
class LocationHelper {
    val TAG = "LocationHelper"
    private val UPDATE_LOCATION_INTERVAL = 1000 * 60 * 30 //30分钟刷新一次位置
    var mContext: Context? = null
    var locationManager: LocationManager? = null
    var locationProvider: String? = null
    var mLocation: Location? = null
    var mOnLocationListener: OnLocationListener? = null

    private object Holder { val instance = LocationHelper()}

    companion object {
        val  instance: LocationHelper by lazy { Holder.instance }
    }

    @SuppressLint("MissingPermission")
    fun  initLocation (context: Context) {
        Log.e(TAG, "initLocation()......")
        mContext = context
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        //2.获取位置提供器，GPS或是NetWork
        val providers = locationManager?.getProviders(true)
        locationProvider = if (providers?.contains(LocationManager.NETWORK_PROVIDER)!!) {
            //如果是网络定位
            Log.d(TAG, "如果是网络定位")
            LocationManager.NETWORK_PROVIDER
        } else if (providers?.contains(LocationManager.GPS_PROVIDER)!!) {
            //如果是GPS定位
            Log.e(TAG, "如果是GPS定位")
            LocationManager.GPS_PROVIDER
        } else {
            Log.e(TAG, "没有可用的位置提供器")
            ""
        }

        Log.e(TAG, "initLocation()......locationProvider = $locationProvider")

        if (!PermissionHelper.isPermissionGranted(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION)
                && !PermissionHelper.isPermissionGranted(mContext!!,Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.e(TAG, "没有定位权限")
            mOnLocationListener?.OnLocation(null,AmapLocationUtil.NO_LOCATION_PERMISSION,"没有定位权限")
            return
        }

        if (!isGpsServiceOpen(mContext)) {
            Log.e(TAG, "定位服务未开启")
            mOnLocationListener?.OnLocation(null,AmapLocationUtil.GPS_NOT_OPEN,"定位服务未开启")
            return
        }

        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
        if (!TextUtils.isEmpty(locationProvider)) {
            var lastLocation = locationManager?.getLastKnownLocation(locationProvider)
            if (lastLocation != null) {
                Log.e(TAG, "initLocation()......set lastLocation")
                setLocation(lastLocation)
            } else {
                Log.e(TAG, "initLocation()......start to location")
                locationManager?.requestLocationUpdates(locationProvider, UPDATE_LOCATION_INTERVAL.toLong(), 0f, locationListener)
            }
        } else {
            Log.e(TAG, "initLocation()......locationProvider is empty")
            mOnLocationListener?.OnLocation(null,AmapLocationUtil.LOCATION_FAILED,"没有可用的位置提供器")
        }
    }

    // 移除定位监听
    @SuppressLint("MissingPermission")
    fun removeLocationUpdatesListener() {
        // 需要检查权限,否则编译不过
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(mContext!!,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                !== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                !== PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
    }

    private fun setLocation(location: Location) {
        mLocation = location
        Log.e(TAG, "纬度-latitude = " + mLocation?.latitude)
        Log.e(TAG, "经度-longitude = " + mLocation?.longitude)

        getAddress(mLocation!!)
    }

    //获取经纬度
    fun getLocation(context: Context,listener: OnLocationListener?): Location? {
        Log.e(TAG, "getLocation()......")
        mOnLocationListener = listener
        mContext = context
        if (mLocation == null) {
            initLocation(mContext!!)
        } else {
            Log.e(TAG, "getLocation()......getAddress")
            getAddress(mLocation!!)
        }
        return mLocation
    }
    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    var locationListener: LocationListener = object : LocationListener {
        /**
         * 当某个位置提供者的状态发生改变时
         */
        override fun onStatusChanged(provider: String, status: Int, arg2: Bundle) {
            Loger.e(TAG,"onStatusChanged......")
        }

        /**
         * 某个设备打开时
         */
        override fun onProviderEnabled(provider: String) {
            Loger.e(TAG,"onProviderEnabled......")
        }

        /**
         * 某个设备关闭时
         */
        override fun onProviderDisabled(provider: String) {
            Loger.e(TAG,"onProviderDisabled......")
        }

        /**
         * 手机位置发生变动
         */
        override fun onLocationChanged(location: Location) {
            Loger.e(TAG,"onLocationChanged......")
            setLocation(location)
        }
    }

    /**
     * 获取地址信息:城市、街道等信息
     */
    fun getAddress(location: Location): List<Address>? {
        Log.e(TAG, "getAddress()......")
        var result:List<Address>?  = null
        try {
            if (location != null) {
                var geocoder = Geocoder(mContext, Locale.getDefault())
                result = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                Loger.e("TAG", "getAddress()-result = "+ JsonUtils.toJSONString(result))

                if (result != null && result.size > 0) {
//                    mOnLocationListener?.OnLocation(result.get(0),null)
                } else {
                    mOnLocationListener?.OnLocation(null,AmapLocationUtil.LOCATION_FAILED,"定位失败")
                }
            } else {
                mOnLocationListener?.OnLocation(null,AmapLocationUtil.LOCATION_FAILED,"定位失败")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mOnLocationListener?.OnLocation(null,AmapLocationUtil.LOCATION_FAILED,"定位失败")
        }
        return result
    }

    /**
     * 检测GPS定位服务是否打开
     * @return
     */
    fun isGpsServiceOpen(context: Context?): Boolean {
        var isGpsServiceOpen = false
        if (context == null) return isGpsServiceOpen
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        isGpsServiceOpen = gps || network
        Log.e(TAG, "isGpsServiceOpen()-isGpsServiceOpen = $isGpsServiceOpen")
        return isGpsServiceOpen
    }

}