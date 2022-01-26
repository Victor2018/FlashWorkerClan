package com.flash.worker.module.business.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.ComplaintParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.DisputeVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportAppealData
import com.flash.worker.module.business.data.ReportMatterData
import com.flash.worker.module.business.interfaces.OnAppealSelectListener
import com.flash.worker.lib.common.view.adapter.RelatedCertificateAdapter
import com.flash.worker.module.business.view.adapter.ReportAppealAdapter
import com.flash.worker.module.business.view.adapter.ReportMatterAdapter
import com.flash.worker.module.business.view.dialog.EmployerComplaintAppealDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_complaint.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ComplaintActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class ComplaintActivity: BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
    OnUploadListener {

    var mLoadingDialog: LoadingDialog? = null

    var mReportMatterAdapter: ReportMatterAdapter? = null
    var mRelatedCertificateAdapter: RelatedCertificateAdapter? = null
    var mReportAppealAdapter: ReportAppealAdapter? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var mComplaintConfirmDetailData: ComplaintConfirmDetailData? = null

    var userName: String? = null
    var jobOrderId: String? = null
    var taskType: Int = 0//1，工单；2，任务

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val disputeVM: DisputeVM by viewModels {
        InjectorUtils.provideDisputeVMFactory(this)
    }


    companion object {
        fun  intentStart (
            activity: AppCompatActivity,
            userName: String?,jobOrderId: String?,
            taskType: Int) {
            var intent = Intent(activity, ComplaintActivity::class.java)
            intent.putExtra(Constant.USER_NAME_KEY,userName)
            intent.putExtra(Constant.INTENT_DATA_KEY,jobOrderId)
            intent.putExtra(Constant.TASK_TYPE_KEY,taskType)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_complaint

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mReportMatterAdapter = ReportMatterAdapter(this,this)
        mRvMatter.adapter = mReportMatterAdapter

        mReportAppealAdapter = ReportAppealAdapter(this,this)
        mRvAppeal.adapter = mReportAppealAdapter

        mRelatedCertificateAdapter = RelatedCertificateAdapter(this, this)
        mRvCertificate.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        mRvCertificate.adapter = mRelatedCertificateAdapter

        mIvBack.setOnClickListener(this)
        mTvMatter.setOnClickListener(this)
        mTvAppeal.setOnClickListener(this)
        mTvComplaint.setOnClickListener(this)
        mTvCancel.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        userName = intent?.getStringExtra(Constant.USER_NAME_KEY)
        jobOrderId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        taskType = intent?.getIntExtra(Constant.TASK_TYPE_KEY,0) ?: 0

        mTvComplaintUserName.text = userName

        mRelatedCertificateAdapter?.add(WorkPicInfo())
        mRelatedCertificateAdapter?.notifyDataSetChanged()

        if (taskType == 1) {
            tv_tip_content.text = "温馨提示：\n发起投诉后，本次雇用立即终止，\n工单将进入争议处理流程。"
        } else if (taskType == 2) {
            tv_tip_content.text = "温馨提示：\n发起投诉后，该人才将停止工作，\n任务将进入争议处理，处理时间约为3—15天。"
        }

        sendUploadConfigRequest()
        sendComplaintConfirmDetailRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.ADD_COMPLAINT_MATTER)
            .observe(this, Observer {
                var matters = it as ArrayList<ReportMatterData>

                mReportMatterAdapter?.clear()
                mReportMatterAdapter?.add(matters)
                mReportMatterAdapter?.notifyDataSetChanged()
            })

        LiveDataBus.with(BusinessActions.ADD_COMPLAINT_APPEAL)
            .observe(this, Observer {
                var appeals = it as ArrayList<ReportAppealData>

                mReportAppealAdapter?.clear()
                mReportAppealAdapter?.add(appeals)
                mReportAppealAdapter?.notifyDataSetChanged()
            })
    }

    fun subscribeUi() {
        fileVM.uploadConfigData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    uploadConfigReq = it.value
                    uploadImgae2Oss()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        disputeVM.complaintConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mComplaintConfirmDetailData = it.value.data
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        disputeVM.complaintData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    UMengEventModule.report(this, BussinessEmployerEvent.employer_complaint_talent)
                    showComplaintSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendComplaintConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        disputeVM.fetchComplaintConfirmDetail(token,jobOrderId)
    }

    fun sendComplaintRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ComplaintParm()
        body.jobOrderId = jobOrderId
        body.complaintItems = getComplaintItems()
        body.complaintPics = getComplaintPics()

        var compensationCreditAmount = getCompensationCreditAmount()
        if (compensationCreditAmount > 0) {
            body.applyCompensationCreditAmount = compensationCreditAmount
        }
        var compensationPrepaidAmount = getCompensationPrepaidAmount()
        if (compensationPrepaidAmount > 0) {
            body.applyCompensationPrepaidAmount = compensationPrepaidAmount
        }

        disputeVM.complaint(token,body)
    }

    fun uploadImgae2Oss () {
        if(uploadConfigReq == null) {
            sendUploadConfigRequest()
            return
        }

        if (selectList == null || selectList.size == 0) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.show(R.string.network_error)
            return
        }

        val uploadData = UploadData()
        uploadData.dir = uploadConfigReq?.data?.modelMap?.get("works")?.dir
        uploadData.bucketName = uploadConfigReq?.data?.modelMap?.get("works")?.bucket
        uploadData.ossEndPoint = uploadConfigReq?.data?.modelMap?.get("works")?.endpoint

        uploadData.localMedia = selectList.get(0)


        mLoadingDialog?.show()

        OssUploadModule.instance.upload(uploadData,this)
    }

    fun showComplaintTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t请确认您的投诉事项真实，" +
                "若您提交虚假投诉，平台将严肃处理！ "
        commonTipDialog.mCancelText = "返回核实"
        commonTipDialog.mOkText = "提交投诉"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendComplaintRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showComplaintSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t您已成功提交投诉，人才将在三日内进行回复；超过三日人才未回复，平台将介入调解！请耐心等待！\n" +
                "\t\t\t\t闪工族感谢您的理解与支持！"
        commonTipDialog.mOkText = "返回"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showComplaintCancelDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t感谢您对人才的理解和宽容！\n" +
                "\t\t\t\t闪工族感谢您的理解与支持！"
        commonTipDialog.mOkText = "返回"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvMatter -> {
                var matterDatas = mReportMatterAdapter?.getDatas()
                        as ArrayList<ReportMatterData> ?
                ComplaintMatterActivity.intentStart(this,matterDatas,taskType)
            }
            R.id.mTvAppeal -> {
                var appealDatas = mReportAppealAdapter?.getDatas() as ArrayList<ReportAppealData>?
                ComplaintAppealActivity.intentStart(this,appealDatas, mComplaintConfirmDetailData)
            }
            R.id.mTvComplaint -> {
                var complaintItems = getComplaintItems()
                if (complaintItems?.size == 0) {
                    ToastUtils.show("请选择投诉事项")
                    return
                }
                if (!hasSelectAppeal()) {
                    ToastUtils.show("请选择投诉诉求")
                    return
                }
                showComplaintTipDlg()
            }
            R.id.mTvCancel -> {
                showComplaintCancelDlg()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
        when (view?.id) {
            R.id.mClMatterRoot -> {
            }
            R.id.mIvRemoveMatter -> {
                mReportMatterAdapter?.removeItem(position)
                mReportMatterAdapter?.notifyItemRemoved(position)
            }
            R.id.mClCertificateRoot -> {
                if (position == count - 1) {
                    var maxCount = 4 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加3张")
                        return
                    }

                    PictureSelectorUtil.selectMedia(this,false,true,
                        false,maxCount)
                }
            }
            R.id.mIvRelatedCertificateDel -> {
                mRelatedCertificateAdapter?.removeItem(position)

                count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0

                var lastPosition = count - 1
                var isAddIcon = TextUtils.isEmpty(mRelatedCertificateAdapter?.getItem(lastPosition)?.pic)

                if (!isAddIcon && count < 3) {
                    mRelatedCertificateAdapter?.add(WorkPicInfo())
                }
                mRelatedCertificateAdapter?.notifyItemRemoved(position)
            }
            R.id.mClAppealRoot -> {

            }
            R.id.mIvRemoveAppeal -> {
                mReportAppealAdapter?.removeItem(position)
                mReportAppealAdapter?.notifyItemRemoved(position)
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
                    Loger.e(BaseFragment.TAG,"onActivityResult-res = " + JsonUtils.toJSONString(list.get(0)))
                    selectList.clear()
                    selectList.addAll(list)

                    uploadImgae2Oss()

                    PictureSelector.obtainMultipleResult(data).clear()
                }
            }
        }
    }

    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"url = $url")

                var data = WorkPicInfo()
                data.pic = url
                var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
                mRelatedCertificateAdapter?.removeItem(count - 1)
                mRelatedCertificateAdapter?.add(data)
                if (count < 3) {
                    mRelatedCertificateAdapter?.add(WorkPicInfo())
                }
                mRelatedCertificateAdapter?.notifyDataSetChanged()

                if (selectList != null && selectList.size > 1) {
                    selectList.removeAt(0)
                    uploadImgae2Oss()
                }
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    fun hasAddAppeal (id: Int): Boolean {
        mReportAppealAdapter?.getDatas()?.forEach {
            if (it.id == id) {
                return true
            }
        }
        return false
    }

    fun getComplaintItems(): List<String>? {
        var complaintItems = ArrayList<String>()
        var count = mReportMatterAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mReportMatterAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.name)) {
                    it.name?.let { it1 -> complaintItems.add(it1) }
                }
            }
        }

        return complaintItems
    }

    fun getComplaintPics(): List<String>? {
        var complaintPics = ArrayList<String>()
        var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mRelatedCertificateAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    it.pic?.let { it1 -> complaintPics.add(it1) }
                }
            }
        }
        return complaintPics
    }

    fun hasSelectAppeal ():Boolean {
        var count = mReportAppealAdapter?.getContentItemCount() ?: 0
        return count > 0
    }

    fun getCompensationCreditAmount(): Double {
        var compensationCreditAmount: Double = 0.0
        var count = mReportAppealAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mReportAppealAdapter?.getDatas()?.forEach {
                if (it.id == 1 && it.compensationCreditAmount > 0) {
                    compensationCreditAmount = it.compensationCreditAmount
                }
            }
        }
        return compensationCreditAmount
    }

    fun getCompensationPrepaidAmount(): Double {
        var compensationPrepaidAmount: Double = 0.0
        var count = mReportAppealAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mReportAppealAdapter?.getDatas()?.forEach {
                if (it.id == 2 && it.compensationPrepaidAmount > 0) {
                    compensationPrepaidAmount = it.compensationPrepaidAmount
                }
            }
        }
        return compensationPrepaidAmount
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }

}