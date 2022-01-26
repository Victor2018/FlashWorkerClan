package com.flash.worker.lib.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.amap.api.maps.model.LatLng
import com.flash.worker.lib.coremodel.util.AppUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MapUtils
 * Author: Victor
 * Date: 2021/10/19 17:33
 * Description: 调用高德地图和腾讯地图需要传入GCJ02坐标系坐标，调用百度地图需要传入BD09坐标系坐标。
 * 因为我的项目中用的是高德地图sdk，所以调用百度地图需要将坐标转换格式。
 * 目前坐标系有三种，分别是WGS84、GCJ02、BD09，国内基本用的是后两种
 * WGS84：国际坐标系，为一种大地坐标系，也是目前广泛使用的GPS全球卫星定位系统使用的坐标系。
 * GCJ02：火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。高德、腾讯都是用的这种。
 * BD09：为百度坐标系，在GCJ02坐标系基础上再次加密。其中BD09ll表示百度经纬度坐标，BD09mc表示百度墨卡托米制坐标。
 * 百度地图sdk默认输出的是BD09ll，定位sdk默认输出的是GCJ02。
————————————————
版权声明：本文为CSDN博主「Ever69」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/Ever69/article/details/82427085
 * -----------------------------------------------------------------
 */
object MapUtils {
    private const val BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap"
    private const val GAODE_PACKAGE_NAME = "com.autonavi.minimap"
    private const val TENCENT_PACKAGE_NAME = "com.tencent.map"

    /**
     *
     * @param scale 比例尺 单位 米
     * @return zoom值
     */
    fun scale2Zoom(scale: Int): Float {
        if (scale <= 1) return 20f
        else if (scale <= 10)
            return 19f
        else if (scale <= 25)
            return 18f
        else if (scale <= 50)
            return 17f
        else if (scale <= 100)
            return 16f
        else if (scale <= 200)
            return 15f
        else if (scale <= 500)
            return 14f
        else if (scale <= 1000)
            return 13f
        else if (scale <= 2000)
            return 12f
        else if (scale <= 5000)
            return 11f
        else if (scale <= 10000)
            return 10f
        else if (scale <= 20000)
            return 9f
        else if (scale <= 30000)
            return 8f
        else if (scale <= 50000)
            return 7f
        else if (scale <= 100000)
            return 6f
        else if (scale <= 200000)
            return 5f
        else if (scale <= 500000)
            return 4f
        else if (scale <= 1000000)
            return 3f
        else if (scale > 1000000)
            return 2f
        return 20f
    }

    /**
     * 是否安装百度地图
     */
    fun haveBaiduMap(context: Context): Boolean {
        return AppUtil.isAppExist(context, BAIDU_PACKAGE_NAME)
    }

    /**
     * 是否安装高德地图
     */
    fun haveGaodeMap(context: Context): Boolean {
        return AppUtil.isAppExist(context, GAODE_PACKAGE_NAME)
    }

    /**
     * 是否安装腾讯地图
     */
    fun haveTencentMap(context: Context): Boolean {
        return AppUtil.isAppExist(context, TENCENT_PACKAGE_NAME)
    }

    /**
     * 调用高德地图app,导航
     *
     * @param context            上下文
     * @param destination        目标经纬度
     * @param destinationAddress 目标地址
     * 高德地图：http://lbs.amap.com/api/amap-mobile/guide/android/route
     */
    fun openGaodeMap(
        context: Context, destination: LatLng?,
        destinationAddress: String
    ) {
        if (!haveGaodeMap(context)) {
            ToastUtils.show("请安装高德地图！")
            return
        }
        if (destination == null) return
        if (TextUtils.isEmpty(destinationAddress)) return
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.addCategory("android.intent.category.DEFAULT")

        val uri = "amapuri://route/plan/?" +
                "dlat=${destination.latitude}" +
                "&dlon=${destination.longitude}" +
                "&dname=$destinationAddress&dev=0&t=0"
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

    /**
     * 调用百度地图
     *
     * @param destination        目的地经纬度
     * @param destinationAddress 目的地地址
     * 百度参考网址：http://lbsyun.baidu.com/index.php?title=uri/api/android
     */
    fun openBaiduMap(
        context: Context, destination: LatLng?,
        destinationAddress: String
    ) {
        if (!haveBaiduMap(context)) {
            ToastUtils.show("请安装百度地图！")
            return
        }
        if (destination == null) return
        if (TextUtils.isEmpty(destinationAddress)) return

        var latLng = GCJ2BD(destination)
        val intent = Intent()
        val uri = "baidumap://map/direction?" +
                "destination=latlng:${latLng?.latitude}, " +
                "${latLng?.longitude}|name:${destinationAddress}&mode=driving"
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

    /**
     * 调用腾讯地图
     *
     * @param context
     * @param destination        目的地经纬度
     * @param destinationAddress 目的地地址
     * 腾讯地图参考网址：http://lbs.qq.com/uri_v1/guide-route.html
     */
    fun openTentcentMap(context: Context, destination: LatLng?, destinationAddress: String) {
        if (!haveTencentMap(context)) {
            ToastUtils.show("请安装腾讯地图！")
            return
        }
        if (destination == null) return
        if (TextUtils.isEmpty(destinationAddress)) return
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = "qqmap://map/routeplan?type=drive&from=&fromcoord=" +
                "&to=${destinationAddress}" +
                "&tocoord=${destination.latitude}," +
                "${destination.longitude}" +
                "&policy=0&referer=appName"
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

    /**
     * 打开网页版 导航
     * 不填我的位置，则通过浏览器定未当前位置
     *
     * @param context
     * @param myLatLng 起点经纬度
     * @param myAddress 起点地址名展示
     * @param destination 终点经纬度
     * @param destinationAddress 终点地址名展示
     */
    fun openBrowserMap(
        context: Context, myLatLng: LatLng, myAddress: String, destination: LatLng?,
        destinationAddress: String
    ) {
        if (destination == null) return
        if (TextUtils.isEmpty(destinationAddress)) return
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.data = Uri.parse(
            ("http://uri.amap.com/navigation?" +
                    "from=${myLatLng.longitude},${myLatLng.latitude},$myAddress" +
                    "to=${destination.longitude},${destination.latitude},$destinationAddress" +
                    "&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0")
        )
        context.startActivity(intent)
    }

    /**
     * BD-09 坐标转换成 GCJ-02 坐标
     */
    fun BD2GCJ(bd: LatLng): LatLng? {
        val x = bd.longitude - 0.0065
        val y = bd.latitude - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI)
        val lng = z * Math.cos(theta) //lng
        val lat = z * Math.sin(theta) //lat
        return LatLng(lat, lng)
    }

    /**
     * GCJ-02 坐标转换成 BD-09 坐标
     */
    fun GCJ2BD(bd: LatLng): LatLng? {
        val x = bd.longitude
        val y = bd.latitude
        val z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI)
        val theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI)
        val tempLon = z * Math.cos(theta) + 0.0065
        val tempLat = z * Math.sin(theta) + 0.006
        return LatLng(tempLat, tempLon)
    }
}