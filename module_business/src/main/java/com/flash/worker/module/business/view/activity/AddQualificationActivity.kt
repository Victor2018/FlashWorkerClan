package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.ResumeCertificateInfo
import com.flash.worker.module.business.R
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_add_qualification.*
import kotlinx.android.synthetic.main.activity_add_qualification.mIvBack
import kotlinx.android.synthetic.main.activity_add_qualification.mTvConfirm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AddQualificationActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class AddQualificationActivity: BaseActivity(),View.OnClickListener {

    var mLoadingDialog: LoadingDialog? = null

    var mResumeCertificateInfo: ResumeCertificateInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: ResumeCertificateInfo?) {
            var intent = Intent(activity, AddQualificationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_add_qualification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        val data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data != null) {
            mResumeCertificateInfo = (data as ResumeCertificateInfo)
            mEtQualificationName.setText(mResumeCertificateInfo?.name)
        }
    }

    fun subscribeUi() {
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
            mTvConfirm -> {
                addQualificationAction()
            }
        }
    }

    fun addQualificationAction () {
        var name = mEtQualificationName.text.toString()
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show("请输入证书名称")
            return
        }

        var data = ResumeCertificateInfo()
        data.name = name

        if (mResumeCertificateInfo == null) {
            LiveDataBus.send(BusinessActions.ADD_QUALIFICATION,data)
        } else {
            data.index = mResumeCertificateInfo?.index ?: 0
            LiveDataBus.send(BusinessActions.UPDATE_QUALIFICATION,data)
        }

        onBackPressed()
    }

}