package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.widget.WheelView
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import com.flash.worker.lib.coremodel.data.bean.CityInfo
import com.flash.worker.lib.coremodel.data.bean.ProvinceInfo
import kotlinx.android.synthetic.main.dlg_city_picker.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CityPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 城市选择dialog
 * -----------------------------------------------------------------
 */
class CityPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener,
        WheelView.OnItemSelectedListener {
    val TAG = "CityPickerDialog"
    var provinceDatas: List<ProvinceInfo>? = null
    var mOnCityPickerListener: OnCityPickerListener? = null

    var mTitleText: String? = null
    var mCancelText: String? = null
    var mOkText: String? = null

    var showAreaPicker: Boolean = true

    var mProvince: String? = App.get().mProvince
    var mCity: String? = App.get().mCity
    var mArea: String? = null

    var provincePosition: Int = 0
    var cityPosition: Int = 0
    var areaPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_city_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initialize()
    }

    fun initialize () {
        initProvincePicker()
        setPickerListener()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        if (!TextUtils.isEmpty(mCancelText)) {
            mTvCancel.text = mCancelText
        }
        if (!TextUtils.isEmpty(mOkText)) {
            mTvConfirm.text = mOkText
        }
        if (!TextUtils.isEmpty(mTitleText)) {
            mTvTitle.text = mTitleText
        }
    }

    fun initData () {
        initPickerCheckIndex()
    }

    fun setPickerListener () {
        mProvincePicker.mOnItemSelectedListener = this
        mCityPicker.mOnItemSelectedListener = this
        mAreaPicker.mOnItemSelectedListener = this
    }

    fun initPickerCheckIndex () {
        Loger.e(TAG,"initIndex-mProvince = " + mProvince)
        Loger.e(TAG,"initIndex-mCity = " + mCity)
        Loger.e(TAG,"initIndex-mArea = " + mArea)
        if (provinceDatas == null) return

        try {
            for (i in 0 until provinceDatas?.size!!) {
                if (TextUtils.equals(mProvince,provinceDatas?.get(i)?.name)) {
                    provincePosition = i
                }
            }

            for (i in 0 until provinceDatas?.get(provincePosition)?.childs?.size!!) {
                if (TextUtils.equals(mCity,provinceDatas?.get(provincePosition)?.childs?.get(i)?.name)) {
                    cityPosition = i
                }
            }

            for (i in 0 until provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.size!!) {
                if (TextUtils.equals(mArea,provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.get(i)?.name)) {
                    areaPosition = i
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initProvincePicker() {
        if (provinceDatas == null) return

        App.get().setCityData(provinceDatas)

        var provinceValue: MutableList<String> = ArrayList()
        var cityValue: MutableList<String> = ArrayList()
        var areaValue: MutableList<String> = ArrayList()

        for (i in 0 until provinceDatas?.size!!) {
            provinceValue.add(provinceDatas?.get(i)?.name!!)
        }

        for (i in 0 until provinceDatas?.get(provincePosition)?.childs?.size!!) {
            cityValue.add(provinceDatas?.get(provincePosition)?.childs?.get(i)?.name!!)
        }

        if (provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs != null &&
                provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.size!! > 0 && showAreaPicker) {
            mAreaPicker.visibility = View.VISIBLE
            for (i in 0 until provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.size!!) {
                areaValue.add(provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.get(i)?.name!!)
            }
            mAreaPicker.setData(areaValue)
        } else {
            mAreaPicker.visibility = View.GONE
        }

        mProvincePicker.setData(provinceValue)
        mCityPicker.setData(cityValue)

        mProvincePicker.setSelectedItemPosition(provincePosition)
        mCityPicker.setSelectedItemPosition(cityPosition)
        mAreaPicker.setSelectedItemPosition(areaPosition)

    }

    fun initCityPicker (provincePosition: Int) {
        var cityValue: MutableList<String> = ArrayList()
        for (i in 0 until provinceDatas?.get(provincePosition)?.childs?.size!!) {
            cityValue.add(provinceDatas?.get(provincePosition)?.childs?.get(i)?.name!!)
        }
        mCityPicker.setData(cityValue)
    }
    fun initAreaPicker (cityPosition: Int) {
        var areaValue: MutableList<String> = ArrayList()

        if (provinceDatas?.get(getCurrentProvincefPosition())?.childs?.get(cityPosition)?.childs != null &&
                provinceDatas?.get(getCurrentProvincefPosition())?.childs?.get(cityPosition)?.childs?.size!! > 0 && showAreaPicker) {
            mAreaPicker.visibility = View.VISIBLE
            for (i in 0 until provinceDatas?.get(getCurrentProvincefPosition())?.childs?.get(cityPosition)?.childs?.size!!) {
                areaValue.add(provinceDatas?.get(getCurrentProvincefPosition())?.childs?.get(cityPosition)?.childs?.get(i)?.name!!)
            }
            mAreaPicker.setData(areaValue)
        } else {
            mAreaPicker.visibility = View.GONE
        }
    }

    fun getCurrentProvincefPosition(): Int {
        var position = mProvincePicker.getSelectedItemPosition()
        return position
    }

    fun getCurrentProvince() : ProvinceInfo {
        var provincePosition = mProvincePicker.getSelectedItemPosition()
        return provinceDatas?.get(provincePosition)!!
    }

    fun getCurrentCity() : CityInfo {
        var provincePosition = mProvincePicker.getSelectedItemPosition()
        var cityPosition = mCityPicker.getSelectedItemPosition()
        return provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)!!
    }

    fun getCurrentArea() : AreaInfo? {
        if (mAreaPicker.visibility == View.GONE) {
            return null
        }
        var provincePosition = mProvincePicker.getSelectedItemPosition()
        var cityPosition = mCityPicker.getSelectedItemPosition()
        var areaPosition = mAreaPicker.getSelectedItemPosition()
        return provinceDatas?.get(provincePosition)?.childs?.get(cityPosition)?.childs?.get(areaPosition)!!
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                mOnCityPickerListener?.OnCityPicker(null,null,null)
                dismiss()
            }
            mTvConfirm -> {
                mOnCityPickerListener?.OnCityPicker(getCurrentProvince(),getCurrentCity(),getCurrentArea())
                dismiss()
            }
        }
    }

    override fun onItemSelected(wheelView: WheelView?, data: String, position: Int) {
        when (wheelView?.id) {
            R.id.mProvincePicker -> {
                initCityPicker(position)
                initAreaPicker(0)
            }
            R.id.mCityPicker -> {
                initAreaPicker(position)
            }
        }
    }

}