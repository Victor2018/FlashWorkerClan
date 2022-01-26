package com.flash.worker.module.message.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnResumeSelectListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.MyResumeDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.frag_bottom_more.*
import permission.victor.com.library.PermissionHelper


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BottomMoreFragment
 * Author: Victor
 * Date: 2021/5/10 16:54
 * Description: 更多面板
 * -----------------------------------------------------------------
 */
class BottomMoreFragment: BaseFragment(),View.OnClickListener, OnResumeSelectListener {

    companion object {

        fun newInstance(account: String?): BottomMoreFragment {
            return newInstance(0,account)
        }
        fun newInstance(id: Int, account: String?): BottomMoreFragment {
            val fragment = BottomMoreFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            bundle.putString(Constant.NIM_ACCOUNT_KEY, account)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var resumeVM: ResumeVM
    private lateinit var employmentVM: EmploymentVM
    private lateinit var userVM: UserVM

    var userAccount: String? = null
    var selectList: MutableList<LocalMedia> = ArrayList()
    var mLoadingDialog: LoadingDialog? = null

    var mMyResumeInfo: MyResumeInfo? = null

    override fun getLayoutResource() = R.layout.frag_bottom_more

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialzie()
        initData()
    }

    fun initialzie () {
        resumeVM = ViewModelProvider(this, InjectorUtils.provideResumeVMFactory(this))
                .get(ResumeVM::class.java)

        employmentVM = ViewModelProvider(this, InjectorUtils.provideEmploymentVMFactory(this))
                .get(EmploymentVM::class.java)

        userVM = ViewModelProvider(this, InjectorUtils.provideUserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mTvSendJob.setOnClickListener(this)
        mTvSendResume.setOnClickListener(this)
        mTvSendImage.setOnClickListener(this)
        mTvLocation.setOnClickListener(this)
    }

    fun initData () {
        userAccount = arguments?.getString(Constant.NIM_ACCOUNT_KEY)
    }

    fun subscribeUi() {
        resumeVM.userResumesData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getResumeDialog(it.value.data).show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.talentResumeDetailData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var data = it.value.data
                    data?.resumeId = mMyResumeInfo?.id
                    LiveDataBus.send(IMActions.SEND_RESUME_MESSAGE,it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.checkTalentBaseInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NavigationUtils.goNewResumeActivity(activity!!)
                    } else {
                        NavigationUtils.goNewTalentBasicActivity(activity!!)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendUserResumeRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        resumeVM.fetchUserResumes(token)
    }

    fun sendResumeDetailRequest (resumeId: String?) {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchTalentResumeDetail(token,resumeId)
    }

    fun sendCheckTalentBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkTalentBaseInfo(token)
    }

    fun getResumeDialog (data: List<MyResumeInfo>?): MyResumeDialog {
        var mMyResumeDialog = MyResumeDialog(activity!!)
        mMyResumeDialog?.resumeList = data
        mMyResumeDialog?.mOnResumeSelectListener = this

        return mMyResumeDialog
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvSendJob -> {
                NavigationUtils.goInviteTalentActivity(activity!!)
            }
            R.id.mTvSendResume -> {
                sendUserResumeRequest()
            }
            R.id.mTvSendImage -> {
                PictureSelectorUtil.selectMedia(this,false,true,false,1)
            }
            R.id.mTvLocation -> {
                if (!AmapLocationUtil.instance.isLocationEnabled(activity)) {
                    showGPSNotOpenDlg()
                    return
                }
                if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                    && !isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        showNoLocationPermissionTipDlg()
                    return
                }
                NavigationUtils.goSendLocationActivity(activity!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val list = PictureSelector.obtainMultipleResult(data)
                    selectList.clear()
                    selectList.addAll(list)

                    PictureSelector.obtainMultipleResult(data).clear()

                    val media = selectList?.get(0)

                    if (media?.isCut) {
                        media.path = media.cutPath
                    }
                    if (media?.isCompressed) {
                        media.path = media.compressPath
                        media.androidQToPath = media.compressPath
                    }

                    var path = media.path
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        path = media.androidQToPath
                    }

                    LiveDataBus.send(IMActions.SEND_IMAGE_MESSAGE,path)

                }
            }
        }
    }

    override fun OnResumeSelect(data: MyResumeInfo?, resumeCount: Int) {
        if (data == null) {
            if (resumeCount >= 5) {
                showAddResumeTipDlg()
                return
            }
            sendCheckTalentBaseInfoRequest()
            return
        }
        mMyResumeInfo = data
        sendResumeDetailRequest(data?.id)
    }

    fun showAddResumeTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您的简历数已达上限可删除后再新增！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showGPSNotOpenDlg () {
        var mGPSNotOpenDlg = CommonTipDialog(activity!!)
        mGPSNotOpenDlg?.mTitle = "温馨提示"
        mGPSNotOpenDlg?.mContent = "定位服务未开启，确定要前往设置？"
        mGPSNotOpenDlg?.mCancelText = "取消"
        mGPSNotOpenDlg?.mOkText = "设置"
        mGPSNotOpenDlg?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                AmapLocationUtil.instance.openGpsService(activity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mGPSNotOpenDlg?.show()
    }

    fun showNoLocationPermissionTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog?.mContent = "定位权限未打开，确定要前往设置？"
        commonTipDialog?.mCancelText = "取消"
        commonTipDialog?.mOkText = "设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                AppUtil.goAppDetailSetting(context!!)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }
}