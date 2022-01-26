package com.flash.worker.module.message.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.*
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.AMapException
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.LocationMessageBean
import kotlinx.android.synthetic.main.activity_send_location.*

@Route(path = ARouterPath.SendLocationAct)
class SendLocationActivity : BaseActivity(), OnMapLoadedListener, OnCameraChangeListener,
    OnMapClickListener, OnLocationListener, GeocodeSearch.OnGeocodeSearchListener,View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SendLocationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var level = 17f //地图的当前的缩放级别

    var aMap: AMap? = null
    var longitude = 0.0
    var latitude = 0.0
    var address: String? = null

    override fun getLayoutResource() = R.layout.activity_send_location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(savedInstanceState)
        initData(intent)
    }

    fun initialize(savedInstanceState: Bundle?) {
        initMapView(savedInstanceState)

        mTvCancel.setOnClickListener(this)
        mTvSend.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(MULTI_PERMISSIONS)
        } else {
            AmapLocationUtil.instance.getLocation(this,this)
        }
    }

    fun initMapView(savedInstanceState: Bundle?) {
        mMvLocation.onCreate(savedInstanceState) // 此方法必须重写

        aMap = mMvLocation.map
        aMap?.setOnMapClickListener(this) //设置amap点击事件
        aMap?.setOnMapLoadedListener(this) // 设置amap加载成功事件监听器
        aMap?.setOnCameraChangeListener(this) // 设置自定义InfoWindow样式
        aMap?.mapType = MAP_TYPE_NORMAL //默认模式

        val uiSettings = aMap?.uiSettings
        uiSettings?.isZoomControlsEnabled = false //隐藏地图缩放控件
        uiSettings?.isRotateGesturesEnabled = false //这个方法设置了地图是否允许通过手势来旋转
        uiSettings?.isTiltGesturesEnabled = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                onBackPressed()
            }
            R.id.mTvSend -> {
                var bean = LocationMessageBean()
                bean.latitude = latitude
                bean.longitude = longitude
                bean.address = address
                LiveDataBus.send(IMActions.SEND_LOCATION_MESSAGE,bean)

                onBackPressed()
            }
        }
    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
        super.onPermissionGranted(permissionName)
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AmapLocationUtil.instance.getLocation(this,this)
        } else {
            setMapCenterLevel()
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        setMapCenterLevel()
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        super.onPermissionDeclined(permissionName)
        setMapCenterLevel()
    }

    override fun onPermissionNeedExplanation(p0: String) {
        super.onPermissionNeedExplanation(p0)
        setMapCenterLevel()
    }

    override fun onMapLoaded() {
        Loger.e(TAG, "setMapCenterLevel()......onMapLoaded")
        setMapCenterLevel()
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
        Loger.e(TAG, "onCameraChangeFinish()......cameraPosition.zoom = " + cameraPosition?.zoom)
        latitude = cameraPosition?.target?.latitude ?: 0.0
        longitude = cameraPosition?.target?.longitude ?: 0.0
        AmapLocationUtil.instance.getAddressByLatlng(this,this,cameraPosition?.target)
    }

    override fun onMapClick(latLng: LatLng?) {
        if (aMap == null) return
        aMap?.animateCamera(CameraUpdateFactory.changeLatLng(latLng))
    }

    override fun OnLocation(location: AMapLocation?, errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......${location?.city}")
        if (errorCode == AmapLocationUtil.NO_LOCATION_PERMISSION) return

        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
        setMapCenterLevel()
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        Loger.e(TAG,"onRegeocodeSearched-rCode = $rCode")
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.regeocodeAddress != null
                && result.regeocodeAddress.formatAddress != null) {
                address = result?.regeocodeAddress.formatAddress
                Loger.e(TAG,"onRegeocodeSearched-address = $address")
                mTvAddress.text = address
                if (TextUtils.isEmpty(address)) {
                    var latLng = LatLng(latitude,longitude)
                    var addr = AmapLocationUtil.instance.searchAddressByLatlng(this,latLng)
                    Loger.e(TAG,"onRegeocodeSearched-addr = ${addr}")

                    address = addr?.featureName

                    if (TextUtils.isEmpty(address)) {
                        var maxAddressLineIndex = addr?.maxAddressLineIndex ?: 0
                        Loger.e(TAG,"onRegeocodeSearched-maxAddressLineIndex = ${maxAddressLineIndex}")
                        address = addr?.getAddressLine(0)
                    }

                    mTvAddress.text = address
                    if (TextUtils.isEmpty(address)) {
                        mTvAddress.visibility = View.GONE
                    } else {
                        mTvAddress.visibility = View.VISIBLE
                    }
                } else {
                    mTvAddress.visibility = View.VISIBLE
                }
            } else {
                mTvAddress.visibility = View.GONE
                ToastUtils.show("error code is $rCode")
            }
        } else {
            mTvAddress.visibility = View.GONE
            if (rCode == 1802 || rCode == 1804 || rCode == 1806) {
                ToastUtils.show(R.string.network_error)
            } else {
                ToastUtils.show("error code is $rCode")
            }
        }
    }

    override fun onGeocodeSearched(result: GeocodeResult?, rCode: Int) {
        Loger.e(TAG,"onGeocodeSearched-rCode = $rCode")
    }


    /**
     * 设置地图中心点击缩放比例
     */
    private fun setMapCenterLevel() {
        Loger.e(TAG, "setMapCenterLevel()......")
        //设置中心点和缩放比例
        if (latitude == 0.0 && longitude == 0.0) return
        var centerMarker = LatLng(latitude, longitude)
        aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMarker, level))
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