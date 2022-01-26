package com.flash.worker.module.message.view.activity

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.*
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment
import kotlinx.android.synthetic.main.activity_view_location.*
import com.amap.api.maps.model.MyLocationStyle
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnMapNavigateListener
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.MapNavigateDialog
import com.flash.worker.lib.coremodel.util.AppUtil


@Route(path = ARouterPath.ViewLocationAct)
class ViewLocationActivity : BaseActivity(), OnMapLoadedListener, OnCameraChangeListener,
    OnMapClickListener, OnLocationListener,View.OnClickListener, OnMapNavigateListener,
    OnMyLocationChangeListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity,locationAttachment: LocationAttachment?) {
            var intent = Intent(activity, ViewLocationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,locationAttachment)
            activity.startActivity(intent)
        }
    }

    var level = 12f //地图的当前的缩放级别

    var aMap: AMap? = null
    var longitude = 0.0
    var latitude = 0.0

    var mLocationAttachment: LocationAttachment? = null
    var mMapNavigateDialog: MapNavigateDialog? = null
    var mGPSNotOpenDlg: CommonTipDialog? = null
    var mNoLocationPermissionTipDialog: CommonTipDialog? = null

    override fun getLayoutResource() = R.layout.activity_view_location

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)
        initialize(savedInstanceState)
        initData(intent)
    }

    fun initialize(savedInstanceState: Bundle?) {
        initMapView(savedInstanceState)

        mIvBack.setOnClickListener(this)
        mIvLocation.setOnClickListener(this)
        mIvNavigate.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mLocationAttachment = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as LocationAttachment?
    }

    fun initMapView(savedInstanceState: Bundle?) {
        mMvLocation.onCreate(savedInstanceState) // 此方法必须重写

        aMap = mMvLocation.map
        aMap?.setOnMapClickListener(this) //设置amap点击事件
        aMap?.setOnMapLoadedListener(this) // 设置amap加载成功事件监听器
        aMap?.setOnCameraChangeListener(this) // 设置地图中心移动监听器
        aMap?.setOnMyLocationChangeListener(this)//设置我的位置更新监听器
        aMap?.mapType = MAP_TYPE_NORMAL //默认模式

        val uiSettings = aMap?.uiSettings
        uiSettings?.isZoomControlsEnabled = false //隐藏地图缩放控件
        uiSettings?.isRotateGesturesEnabled = false //这个方法设置了地图是否允许通过手势来旋转
        uiSettings?.isTiltGesturesEnabled = false

        //设置地图中我的位置小蓝点显示样式
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.interval(30 * 1000)//30s定位一次

//        var bd = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked)
//        myLocationStyle.myLocationIcon(bd)///设置定位蓝点的icon图标

        //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW)//只定位一次

        //设置定位蓝点精度圆圈的边框颜色
//        myLocationStyle.strokeColor(ResUtils.getColorRes(R.color.color_26F7A730))

        //设置定位蓝点精度圈的边框宽度
//        myLocationStyle.strokeWidth(ResUtils.getDimenFloatPixRes(R.dimen.dp_2))

        //设置定位蓝点精度圆圈的填充颜色
//        myLocationStyle.radiusFillColor(ResUtils.getColorRes(R.color.color_26F7A730))

        aMap?.myLocationStyle = myLocationStyle //设置定位蓝点的Style
        aMap?.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    private fun addCircleMakerInScreenCenter() {
        Loger.e(TAG, "addCircleMakerInScreenCenter()......aMap = $aMap")
        Loger.e(TAG, "addCircleMakerInScreenCenter()......mLocationAttachment = $mLocationAttachment")
        if (aMap == null) return
        if (mLocationAttachment == null) return
        aMap?.clear()
        val view: View = LayoutInflater.from(this).inflate(R.layout.map_location_view_cell, null)
        val mTvAddress = view.findViewById<TextView>(R.id.mTvAddress)
        mTvAddress.text = mLocationAttachment?.address
        
        if (TextUtils.isEmpty(mLocationAttachment?.address)) {
            mTvAddress.visibility = View.GONE
        } else {
            mTvAddress.visibility = View.VISIBLE
        }
        
        val markerOptions = MarkerOptions()
        val bitmapDescriptor = BitmapDescriptorFactory.fromView(view)
        markerOptions.anchor(0.5f, 0.5f)
            .position(LatLng(mLocationAttachment?.latitude ?: 0.0,
                mLocationAttachment?.longitude ?: 0.0))
            .icon(bitmapDescriptor)
        aMap?.addMarker(markerOptions)
    }

    fun location () {
        Loger.e(TAG,"location()......")
        if (aMap?.myLocation != null) {
            latitude = aMap?.myLocation?.latitude ?: 0.0
            longitude = aMap?.myLocation?.longitude ?: 0.0
            if (latitude != 0.0 && longitude != 0.0) {
                moveMapCamera(latitude, longitude)
                return
            }
        }

        if (!AmapLocationUtil.instance.isLocationEnabled(this)) {
            showGPSNotOpenDlg()
            return
        }

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(MULTI_PERMISSIONS)
            return
        }

        AmapLocationUtil.instance.getLocation(this,this)
    }

    fun showMapNavigateDlg() {
        if (mMapNavigateDialog == null) {
            mMapNavigateDialog = MapNavigateDialog(this)
            mMapNavigateDialog?.mOnMapNavigateListener = this
        }
        mMapNavigateDialog?.show()
    }

    fun showGPSNotOpenDlg () {
        if (mGPSNotOpenDlg == null) {
            mGPSNotOpenDlg = CommonTipDialog(this)
            mGPSNotOpenDlg?.mTitle = "温馨提示"
            mGPSNotOpenDlg?.mContent = "定位服务未开启，确定要前往设置？"
            mGPSNotOpenDlg?.mCancelText = "取消"
            mGPSNotOpenDlg?.mOkText = "设置"
            mGPSNotOpenDlg?.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
                override fun OnDialogOkClick() {
                    AmapLocationUtil.instance.openGpsService(this@ViewLocationActivity)
                }

                override fun OnDialogCancelClick() {
                }

            }
        }

        mGPSNotOpenDlg?.show()
    }

    fun showNoLocationPermissionTipDlg () {
        if (mNoLocationPermissionTipDialog == null) {
            mNoLocationPermissionTipDialog = CommonTipDialog(this)
            mNoLocationPermissionTipDialog?.mTitle = "温馨提示"
            mNoLocationPermissionTipDialog?.mContent = "定位权限未打开，确定要前往设置？"
            mNoLocationPermissionTipDialog?.mCancelText = "取消"
            mNoLocationPermissionTipDialog?.mOkText = "设置"
            mNoLocationPermissionTipDialog?.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
                override fun OnDialogOkClick() {
                    AppUtil.goAppDetailSetting(this@ViewLocationActivity)
                }

                override fun OnDialogCancelClick() {
                }
            }
        }
        mNoLocationPermissionTipDialog?.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvLocation -> {
                location()
            }
            R.id.mIvNavigate -> {
                showMapNavigateDlg()
            }
        }
    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
        super.onPermissionGranted(permissionName)
        Loger.e(TAG,"onPermissionGranted......")
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AmapLocationUtil.instance.getLocation(this,this)
        } else {
            moveMapCamera(latitude, longitude)
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        Loger.e(TAG,"onPermissionReallyDeclined()......")
        showNoLocationPermissionTipDlg()
    }

    override fun onMapLoaded() {
        addCircleMakerInScreenCenter()
        if (mLocationAttachment != null) {
            moveMapCamera(mLocationAttachment?.latitude, mLocationAttachment?.longitude)
        }
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
        Loger.e(TAG, "onCameraChangeFinish()......cameraPosition.zoom = " + cameraPosition?.zoom)
    }

    override fun onMapClick(latLng: LatLng?) {
        aMap?.animateCamera(CameraUpdateFactory.changeLatLng(latLng))
    }

    override fun onMyLocationChange(location: Location?) {
        Loger.e(TAG,"onMyLocationChange()......longitude = ${location?.longitude}")
        Loger.e(TAG,"onMyLocationChange()......latitude = ${location?.latitude}")
    }

    override fun OnLocation(location: AMapLocation?, errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......${location?.city}")
        if (errorCode == AmapLocationUtil.NO_LOCATION_PERMISSION) return

        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
        moveMapCamera(latitude, longitude)
    }

    override fun onGaodeNavigate() {
        var longitude = mLocationAttachment?.longitude ?: 0.0
        var latitude = mLocationAttachment?.latitude ?: 0.0
        var latLng = LatLng(latitude,longitude)
        var address = mLocationAttachment?.address ?: "未知地址"
        MapUtils.openGaodeMap(this, latLng, address)
    }

    override fun onBaiduNavigate() {
        var longitude = mLocationAttachment?.longitude ?: 0.0
        var latitude = mLocationAttachment?.latitude ?: 0.0
        var latLng = LatLng(latitude,longitude)
        var address = mLocationAttachment?.address ?: "未知地址"
        MapUtils.openBaiduMap(this, latLng, address)
    }

    override fun onTentcentNavigate() {
        var longitude = mLocationAttachment?.longitude ?: 0.0
        var latitude = mLocationAttachment?.latitude ?: 0.0
        var latLng = LatLng(latitude,longitude)
        var address = mLocationAttachment?.address ?: "未知地址"
        MapUtils.openTentcentMap(this, latLng, address)
    }

    /**
     * 把地图画面移动到定位地点(使用moveCamera方法没有动画效果)
     *
     * @param latitude
     * @param longitude
     */
    private fun moveMapCamera(latitude: Double?, longitude: Double?) {
        if (latitude == 0.0 && longitude == 0.0) return
        var latLng = LatLng(latitude ?: 0.0, longitude ?: 0.0)
        aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, level))
    }

    override fun onResume() {
        super.onResume()
        mMvLocation.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMvLocation.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMvLocation.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        super.onDestroy()
        aMap?.clear()
        mMvLocation.onDestroy()
        AmapLocationUtil.instance.removeLocationListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }


}