package com.flash.worker.lib.common.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.ServiceSettings
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeQuery

import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.coremodel.util.AppConfig
import permission.victor.com.library.PermissionHelper
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.startActivity
import com.flash.worker.lib.coremodel.util.AppUtil


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AmapLocationUtil
 * Author: Victor
 * Date: 2021/8/16 11:26
 * Description: 
 * -----------------------------------------------------------------
 */
class AmapLocationUtil: AMapLocationListener {
    private val TAG = "AmapLocationUtil"

    private val UPDATE_LOCATION_INTERVAL: Long = 1000 * 60 * 30 //30分钟刷新一次位置
    private val HTTP_TIME_OUT:Long = 60000
    private var lastLocationTime: Long = 0//最后获取位置时间
    var mContext: Context? = null
    private var locationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null
    private val listeners: ArrayList<OnLocationListener> = ArrayList()
    var mLocation: AMapLocation? = null
    var mGeocodeSearch: GeocodeSearch? = null
    var mOnGeocodeSearchListener: OnGeocodeSearchListener? = null

    private object Holder { val instance = AmapLocationUtil()}

    companion object {
        val NO_LOCATION_PERMISSION = 0X1021//没有定位权限
        val GPS_NOT_OPEN = 0X1022//定位服务未开启
        val LOCATION_FAILED = 0X1023//定位失败
        val  instance: AmapLocationUtil by lazy { Holder.instance }
    }

    fun loactionUpdatePrivacy (context: Context) {
        ServiceSettings.updatePrivacyShow(context,true,true)
        ServiceSettings.updatePrivacyAgree(context,true)
    }

    fun mapUpdatePrivacy (context: Context) {
//        MapsInitializer.updatePrivacyShow(context,true,true)
//        MapsInitializer.updatePrivacyAgree(context,true)
    }

    fun initLocation (context: Context) {
        loactionUpdatePrivacy(context)
        if (!isLocationEnabled(context)) {
            Log.e(TAG, "定位服务未开启")
            onLocationResult(null,GPS_NOT_OPEN,"定位服务未开启")
            return
        }
        if (!PermissionHelper.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
            && !PermissionHelper.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.e(TAG, "没有定位权限")
            onLocationResult(null,NO_LOCATION_PERMISSION,"没有定位权限")
            return
        }

        initOption()

        if (locationClient == null) {
            locationClient = AMapLocationClient(context)
            //设置定位参数
            locationClient?.setLocationOption(locationOption)
            // 设置定位监听
            locationClient?.setLocationListener(this)
        }

        startLocation()

    }

    private fun initOption () {
        if (locationOption != null) return

        locationOption = AMapLocationClientOption()
        //如果网络可用就选择高精度
        locationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        locationOption?.httpTimeOut = HTTP_TIME_OUT //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationOption?.interval = UPDATE_LOCATION_INTERVAL //可选，设置定位间隔。默认为2秒
        locationOption?.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
        locationOption?.isOnceLocation = true //可选，设置是否单次定位。默认是false
        locationOption?.isOnceLocationLatest = true //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption?.isSensorEnable = true //可选，设置是否使用传感器。默认是false
        locationOption?.isWifiScan = true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption?.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        //设置是否允许模拟位置,默认为true，允许模拟位置
        locationOption?.isMockEnable = AppConfig.MODEL_DEBUG

    }

    override fun onLocationChanged(location: AMapLocation?) {
        Log.e(TAG, "onLocationChanged()......location = $location")
        if (location != null) {
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.errorCode === 0) {
                mLocation = location
                onLocationResult(location, location.errorCode, "")
                lastLocationTime = System.currentTimeMillis()
            } else {
                onLocationResult(null,location.errorCode,  location.errorInfo)
            }
        } else {
            onLocationResult(null,LOCATION_FAILED,"定位失败")
        }

    }

    fun startLocation() {
        Log.e(TAG, "startLocation()......")
        locationClient?.startLocation()
    }

    //获取经纬度
    fun getLocation(context: Context,listener: OnLocationListener) {
        Log.e(TAG, "getLocation()......")
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
        mContext = context
        if (mLocation == null) {
            initLocation(context)
        } else {
            if (System.currentTimeMillis() - lastLocationTime > UPDATE_LOCATION_INTERVAL){
                startLocation()
            } else {
                onLocationResult(mLocation,0,"")
            }
        }
    }

    fun removeLocationListener(listener: OnLocationListener?) {
        mOnGeocodeSearchListener = null
        listeners.remove(listener)
    }

    fun onLocationResult (location: AMapLocation?, errorCode: Int,error: String?) {
        listeners.forEach { 
            it.OnLocation(location,errorCode,error)
        }
    }

    fun getAddressByLatlng(context: Context?,listener: OnGeocodeSearchListener?,latLng: LatLng?) {
        mOnGeocodeSearchListener = listener
        if (mGeocodeSearch == null) {
            mGeocodeSearch = GeocodeSearch(context)
        }
        mGeocodeSearch?.setOnGeocodeSearchListener(mOnGeocodeSearchListener)
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        val latLonPoint = LatLonPoint(latLng?.latitude ?: 0.0, latLng?.longitude ?: 0.0)
        val query = RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP)
        //异步查询
        mGeocodeSearch?.getFromLocationAsyn(query)
    }

    fun searchAddressByLatlng(context: Context?,latLng: LatLng?): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val list = geocoder.getFromLocation(
            latLng?.latitude ?: 0.0, latLng?.longitude ?: 0.0, 1)
        if (list != null && list.size > 0) {
            return list[0]
        }
        return null
    }

    /**
     * 检测GPS定位服务是否打开，部分手机会有问题
     * @return
     */
    fun isGpsServiceOpen(context: Context?): Boolean {
        var isGpsServiceOpen = false
        if (context == null) return isGpsServiceOpen
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        isGpsServiceOpen = gps || network

        return isGpsServiceOpen
    }

    /**
     * 检测定位服务是否打开
     */
    fun isLocationEnabled(context: Context?): Boolean {
        var isLocationEnabled = false
        val locationProviders: String

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                var locationMode = Settings.Secure.getInt(context?.contentResolver, Settings.Secure.LOCATION_MODE)
                isLocationEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(context?.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
                isLocationEnabled = !TextUtils.isEmpty(locationProviders)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isLocationEnabled
    }

    fun openGpsService(context: Context?) {
        if (context == null) return
        val intent = Intent()
        intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        context.startActivity(intent)
    }

}