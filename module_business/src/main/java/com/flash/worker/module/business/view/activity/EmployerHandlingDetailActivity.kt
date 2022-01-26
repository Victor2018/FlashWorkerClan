package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.AgreeComplaintParm
import com.flash.worker.lib.coremodel.data.parm.ApplyPlatformAccessParm
import com.flash.worker.lib.coremodel.data.parm.CancelComplaintParm
import com.flash.worker.lib.coremodel.data.req.DisputeDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.DisputeVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.RelatedCertificateAdapter
import com.flash.worker.module.business.view.adapter.EmployerHandlingDetailAdapter
import com.flash.worker.module.business.view.dialog.PlatformAccessingPopWindow
import kotlinx.android.synthetic.main.activity_employer_handling_detail.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerHandlingDetailActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */

@Route(path = ARouterPath.EmployerHandlingDetailAct)
class EmployerHandlingDetailActivity: BaseActivity(),View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    OnTradePasswordListener {

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerHandlingDetailAdapter: EmployerHandlingDetailAdapter? = null
    var mRelatedCertificateAdapter: RelatedCertificateAdapter? = null

    var complaintNo: String? = null

    var mDisputeDetailReq: DisputeDetailReq? = null

    var passwordErrorCount: Int = 0//密码输入错误次数
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置
    var isCancelComplaint: Boolean = false//是否是取消投诉

    private val disputeVM: DisputeVM by viewModels {
        InjectorUtils.provideDisputeVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,complaintNo: String?) {
            var intent = Intent(activity, EmployerHandlingDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,complaintNo)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_handling_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mEmployerHandlingDetailAdapter = EmployerHandlingDetailAdapter(this,this)
        mRvHandlingDetail.adapter = mEmployerHandlingDetailAdapter

        mRelatedCertificateAdapter = RelatedCertificateAdapter(this, this)
        mRelatedCertificateAdapter?.isPreview = true
        mRvCertificate.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false)
        mRvCertificate.adapter = mRelatedCertificateAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvModifyHistory.setOnClickListener(this)
        mTvOrderNo.setOnClickListener(this)
        mTvApplyPlatform.setOnClickListener(this)
        mTvAgreeAppeal.setOnClickListener(this)
        mTvModify.setOnClickListener(this)
        mTvCancelComplaint.setOnClickListener(this)
        mTvContactTalent.setOnClickListener(this)


        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder = mRvCertificate
                                .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvRelatedCertificate)
                    }
                })
    }

    fun initData (intent: Intent?) {
        complaintNo = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendHandlingDetailRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
                .observeForever(this, Observer {
                    currentWorkImagePositon = it as Int
                })
    }

    fun subscribeUi() {
        disputeVM.disputeDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showHandlingDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        disputeVM.applyPlatformAccessData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("申请平台介入成功")
                    sendHandlingDetailRequest()

                    UMengEventModule.report(this, BussinessEmployerEvent.employer_apply_platform_access)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        disputeVM.cancelComplaintData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("取消投诉成功")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    UMengEventModule.report(this, BussinessEmployerEvent.employer_cancel_complaint)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("151135",it.code)) {
                        if (passwordErrorCount >= 4) {
                            LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                            showErrorPasswordTipDlg()
                            passwordErrorCount = 0
                        }
                        LiveDataBus.send(JobActions.TRADE_PASSWORD_ERROR)
                        passwordErrorCount++
                        return@Observer
                    }
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message)
                }
            }
        })

        disputeVM.agreeComplaintData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("同意诉求成功")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    UMengEventModule.report(this, BussinessEmployerEvent.employer_agree_request)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("151135",it.code)) {
                        if (passwordErrorCount >= 4) {
                            LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                            showErrorPasswordTipDlg()
                            passwordErrorCount = 0
                        }
                        LiveDataBus.send(JobActions.TRADE_PASSWORD_ERROR)
                        passwordErrorCount++
                        return@Observer
                    }
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.imLoginInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    NavigationUtils.goChatActivity(this,it.value.data?.imAccid)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        accountVM.accountInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var hasTradePassword = it.value?.data?.hasTradePassword ?: false
                    if (!hasTradePassword) {
                        showSetTransactionPwdDlg()
                        return@Observer
                    }
                    getTradePasswordDialog().show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendHandlingDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        disputeVM.fetchDisputeDetail(token,complaintNo)
    }

    fun sendApplyPlatformAccessRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ApplyPlatformAccessParm()
        body.complaintNo = complaintNo

        disputeVM.applyPlatformAccess(token,body)
    }

    fun sendCancelComplaintRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CancelComplaintParm()
        body.complaintNo = complaintNo
        body.tradePassword = tradePassword

        disputeVM.cancelComplaint(token,body)
    }

    fun sendAgreeComplaintRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = AgreeComplaintParm()
        body.complaintNo = complaintNo
        body.tradePassword = tradePassword

        disputeVM.agreeComplaint(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun sendAccountInfoRequest (cancelComplaint: Boolean) {
        passwordErrorCount = 0
        isCancelComplaint = cancelComplaint
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showHandlingDetailData (datas: DisputeDetailReq) {
        mDisputeDetailReq = datas
        mTvReportCompany.text = "投诉对象：" + datas.data?.talentUsername
        mTvComplaint.text = getComplaintItemsTxt(datas?.data?.complaintItems)
        mTvAppeal.text = getComplaintRequirementsTxt(datas?.data?.complaintRequirements)

        if (datas.data?.disputeType == 1) {//人才举报
            mTvModifyHistory.text = "变更记录"
            tv_complaint.text = "人才举报："
            tv_appeal.text = "人才诉求："

            var count = datas.data?.disputeProgressList?.size ?: 0
            if (count > 1) {
                mTvTip.visibility = View.GONE
            } else {
                mTvTip.visibility = View.VISIBLE
            }

            mTvReportCompany.visibility = View.GONE
            mTvCancelComplaint.visibility = View.GONE
            if (datas.data?.status == 5) {
                mTvApplyPlatform.visibility = View.VISIBLE
            } else {
                mTvApplyPlatform.visibility = View.GONE
            }

            if (datas.data?.status == 15 || datas.data?.status == 30) {
                mTvAgreeAppeal.visibility = View.GONE
            } else {
                mTvAgreeAppeal.visibility = View.VISIBLE
            }
        } else if (datas.data?.disputeType == 2) {//雇主投诉
            mTvModifyHistory.text = "修改历史"
            tv_complaint.text = "我的投诉："
            tv_appeal.text = "我的诉求："

            mTvTip.visibility = View.GONE
            mTvReportCompany.visibility = View.VISIBLE
            mTvApplyPlatform.visibility = View.GONE
            mTvAgreeAppeal.visibility = View.GONE

            if (datas.data?.status == 15 || datas.data?.status == 25 || datas.data?.status == 30) {
                mTvCancelComplaint.visibility = View.GONE
            } else {
                mTvCancelComplaint.visibility = View.VISIBLE
            }
            //只要没有结束就可以修改
            if (datas.data?.status == 15 || datas.data?.status == 30) {
                mTvModify.visibility = View.GONE
            } else {
                mTvModify.visibility = View.VISIBLE
            }
        }

        mRelatedCertificateAdapter?.clear()
        mRelatedCertificateAdapter?.add(getcomplaintPics(datas?.data?.complaintPics))
        mRelatedCertificateAdapter?.notifyDataSetChanged()

        var picCount = datas?.data?.complaintPics?.size ?: 0
        if (picCount > 0) {
            tv_certificate.show()
            mRvCertificate.show()
        } else {
            tv_certificate.hide()
            mRvCertificate.hide()
        }

        mTvOrderNo.text = "投诉单号：${datas?.data?.complaintNo}"

        mEmployerHandlingDetailAdapter?.clear()
        mEmployerHandlingDetailAdapter?.add(datas.data?.disputeProgressList)
        mEmployerHandlingDetailAdapter?.notifyDataSetChanged()
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@EmployerHandlingDetailActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已连续输错密码5次，\n" +
                "若连续输错密码10次 ，\n" +
                "您的帐户将被锁定2小时。"
        commonTipDialog.mCancelText = "忘记密码"
        commonTipDialog.mOkText = "重试"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                getTradePasswordDialog().show()
            }

            override fun OnDialogCancelClick() {
                var userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 0) {
                    showAuthTipDlg()
                    return
                }
                NavigationUtils.goVerifyIdentidyActivity(this@EmployerHandlingDetailActivity)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@EmployerHandlingDetailActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showApplyPlatformAccessTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t您确定需要申请平台介入吗？\n" +
                "\t\t\t\t平台介入后，若核实人才举报" +
                "属实，您除了需要向人才进行赔付" +
                "外，您的信用积分还将被扣掉10" +
                "分。(同意诉求不扣分。)"
        commonTipDialog.mCancelText = "我在想想"
        commonTipDialog.mOkText = "确定申请"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendApplyPlatformAccessRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCancelComplaintTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t感谢您的理解与支持！\n" +
                "\t\t\t\t取消投诉后，因雇用已终止，您需赔付人才信用保证金:" +
                "${mDisputeDetailReq?.data?.employerCreditAmount}元；" +
                "结算已预付报酬:${mDisputeDetailReq?.data?.prepaidAmount}元。"
        commonTipDialog.mCancelText = "返回，继续投诉"
        commonTipDialog.mOkText = "确定取消"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAccountInfoRequest(true)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showMoidifyComplaintTipDlg (count: Int) {
        if (count  <= 0) {
            ToastUtils.show("修改次数已用完")
            return
        }
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还剩余${count}次修改机会，\n" +
                "是否继续修改?"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                ModifyComplaintActivity.intentStart(this@EmployerHandlingDetailActivity,
                    mDisputeDetailReq?.data)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getComplaintRequirementsTxt (complaintRequirements: List<String>?): String? {
        var complaintItemsSb = StringBuffer()

        var count = complaintRequirements?.size ?: 0
        for (i in 0 until count) {
            if (i < count - 1) {
                complaintItemsSb.append((i+1).toString() + "," + complaintRequirements?.get(i) + "\n")
            } else {
                complaintItemsSb.append((i+1).toString() + "," + complaintRequirements?.get(i))
            }
        }
        return complaintItemsSb.toString()
    }

    fun getComplaintItemsTxt (complaintItems: List<String>?): String? {
        var complaintItemsSb = StringBuffer()

        var count = complaintItems?.size ?: 0
        for (i in 0 until count) {
            if (i < count - 1) {
                complaintItemsSb.append((i+1).toString() + "," + complaintItems?.get(i) + "\n")
            } else {
                complaintItemsSb.append((i+1).toString() + "," + complaintItems?.get(i))
            }
        }
        return complaintItemsSb.toString()
    }

    fun getcomplaintPics (complaintPics: List<String>?): List<WorkPicInfo> {
        var pics = ArrayList<WorkPicInfo>()
        complaintPics?.forEach {
            if (!TextUtils.isEmpty(it)) {
                var item = WorkPicInfo()
                item.pic = it
                pics.add(item)
            }
        }
        return pics
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvModifyHistory -> {
                var disputeType = mDisputeDetailReq?.data?.disputeType ?: 0
                if (disputeType == 1) {//人才举报
                    ReportChangeHistoryActivity.intentStart(this,complaintNo)
                } else if (disputeType == 2) {//雇主投诉
                    ComplaintModifyHistoryActivity.intentStart(this,complaintNo)
                }
            }
            R.id.mTvOrderNo -> {
                val complaintNo = mDisputeDetailReq?.data?.complaintNo
                ClipboardUtil.copy(this,Constant.SGZ_COMPLAINT_NO,complaintNo)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvApplyPlatform -> {
                showApplyPlatformAccessTipDlg()
            }
            R.id.mTvAgreeAppeal -> {
                sendAccountInfoRequest(false)
            }
            R.id.mTvModify -> {
                var count = mDisputeDetailReq?.data?.remainCount ?: 0
                showMoidifyComplaintTipDlg(count)
            }
            R.id.mTvCancelComplaint -> {
                showCancelComplaintTipDlg()
            }
            R.id.mTvContactTalent -> {
                val userId = mDisputeDetailReq?.data?.talentUserId
                sendImLoginInfoRequest(userId)
            }
        }
    }

    override fun onRefresh() {
        mEmployerHandlingDetailAdapter?.clear()
        mEmployerHandlingDetailAdapter?.setFooterVisible(false)
        mEmployerHandlingDetailAdapter?.notifyDataSetChanged()

        mRvHandlingDetail.setHasMore(false)

        sendHandlingDetailRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mTvMessage -> {
                val xOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0) * -1.0f)
                val yOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())

                var disputeType = mDisputeDetailReq?.data?.disputeType ?: 0
                var platformAccessingPopWindow = PlatformAccessingPopWindow(this,true,disputeType)
                platformAccessingPopWindow.show(view!!, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            R.id.mClCertificateRoot -> {
                currentWorkImagePositon = position
                ViewImageActivity.intentStart(this,
                        getViewImageUrls(mRelatedCertificateAdapter?.getDatas()),
                        position,
                        view?.findViewById(R.id.mIvRelatedCertificate),
                        ResUtils.getStringRes(R.string.img_transition_name))
            }
            else -> {

            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        if (isCancelComplaint) {
            sendCancelComplaintRequest(tradePassword)
        } else {
            sendAgreeComplaintRequest(tradePassword)
        }
    }

    fun getViewImageUrls(urls: List<WorkPicInfo>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                it.pic?.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }
}