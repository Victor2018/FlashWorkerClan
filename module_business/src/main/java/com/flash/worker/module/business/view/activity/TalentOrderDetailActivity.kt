package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.adapter.JobWorkPicAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TalentOrderDetailData
import com.flash.worker.lib.coremodel.data.req.TalentOrderDetailReq
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.HomeVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_talent_order_detail.*

@Route(path = ARouterPath.TalentOrderDetailAct)
class TalentOrderDetailActivity : BaseActivity(),AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, jobOrderId: String?) {
            var intent = Intent(activity, TalentOrderDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,jobOrderId)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null
    var jobOrderId: String? = null
    var mJobWorkPicAdapter: JobWorkPicAdapter? = null
    var mTalentOrderDetailData: TalentOrderDetailData? = null
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    private val talentJobVM: TalentJobVM by viewModels {
        InjectorUtils.provideTalentJobVMFactory(this)
    }

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_talent_order_detail

    override fun onCreate(savedInstanceState: Bundle?) {
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

        mJobWorkPicAdapter = JobWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mJobWorkPicAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvCompany.setOnClickListener(this)
        mTvUserName.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        mTvJobOrderId.setOnClickListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder: RecyclerView.ViewHolder = mRvWorksPic
                        .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvJobPic)
                }
            })
    }

    fun initData (intent: Intent?) {
        jobOrderId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendTalentOrderDetailRequest()
    }

    fun subscribeUi() {
        talentJobVM.talentOrderDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentOrderDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
            .observeForever(this, Observer {
                currentWorkImagePositon = it as Int
            })

    }

    fun sendTalentOrderDetailRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentJobVM.fetchTalentOrderDetail(token,jobOrderId)
    }

    fun showTalentOrderDetailData (data: TalentOrderDetailReq) {
        mTalentOrderDetailData = data.data
        mTvCompany.text = data.data?.releaseInfo?.employerName

        var identity = data.data?.releaseInfo?.identity ?: 0
        var employerName = data.data?.releaseInfo?.employerName ?: ""

        setSpannable(identity,employerName)

        var licenceAuth = data.data?.releaseInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        mTvUserName.text = data.data?.releaseInfo?.username
        mTvEmployerCreditScore.text = "信用分: ${data?.data?.releaseInfo?.employerCreditScore}"
        mTvUserId.text = "ID:" + data.data?.releaseInfo?.userId
        mTvTitle.text = "${data?.data?.releaseInfo?.title}(${data?.data?.releaseInfo?.peopleCount}人)"

        if (data.data?.releaseInfo?.settlementMethod == 1) {
            mTvSettlementMethod.text = "日结"
        } else  if (data.data?.releaseInfo?.settlementMethod == 2) {
            mTvSettlementMethod.text = "周结"
        } else  if (data.data?.releaseInfo?.settlementMethod == 3) {
            mTvSettlementMethod.text = "整单结"
        }

        if (data.data?.releaseInfo?.identityRequirement == 1) {
            mTvOnlyStudent.visibility = View.GONE
            line_student_only.visibility = View.GONE
        } else  if (data.data?.releaseInfo?.identityRequirement == 2) {
            mTvOnlyStudent.visibility = View.VISIBLE
            line_student_only.visibility = View.VISIBLE
        } else if (data.data?.releaseInfo?.identityRequirement == 3) {
            mTvOnlyStudent.visibility = View.GONE
            line_student_only.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data.data?.releaseInfo?.eduRequirement)) {
            mTvEducation.visibility = View.GONE
            line_education.visibility = View.GONE
        } else {
            mTvEducation.text = data.data?.releaseInfo?.eduRequirement
            mTvEducation.visibility = View.VISIBLE
            line_education.visibility = View.VISIBLE
        }

        var isAtHome = data.data?.releaseInfo?.isAtHome ?: false
        if (isAtHome) {
            line_do_at_home.visibility = View.VISIBLE
            mTvDoAtHome.visibility = View.VISIBLE
        } else {
            line_do_at_home.visibility = View.GONE
            mTvDoAtHome.visibility = View.GONE
        }

        if (data.data?.releaseInfo?.sexRequirement == 0) {
            mTvSex.text = "女"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (data.data?.releaseInfo?.sexRequirement == 1) {
            mTvSex.text = "男"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (data.data?.releaseInfo?.sexRequirement == 2) {
            mTvSex.text = "不限"
            line_sex.visibility = View.GONE
            mTvSex.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data.data?.releaseInfo?.ageRequirement)) {
            line_age.visibility = View.GONE
            mTvAge.visibility = View.GONE
        } else {
            line_age.visibility = View.VISIBLE
            mTvAge.visibility = View.VISIBLE
            mTvAge.text = "${data.data?.releaseInfo?.ageRequirement}岁"
        }

        mTvWorkDate.text = "${data.data?.releaseInfo?.jobStartTime}-" +
                "${data.data?.releaseInfo?.jobEndTime}(${data.data?.releaseInfo?.totalDays}天)"

        var isBeforeStartTime = DateUtil.isNextDayDate(
            data.data?.releaseInfo?.startTime,data.data?.releaseInfo?.endTime,"HH:mm")
        if (isBeforeStartTime) {
            mTvWorkTime.text = "${data.data?.releaseInfo?.startTime}-次日${data.data?.releaseInfo?.endTime}"
        } else {
            mTvWorkTime.text = "${data.data?.releaseInfo?.startTime}-${data.data?.releaseInfo?.endTime}"
        }

        var paidHour = data.data?.releaseInfo?.paidHour ?: 0.0
        mTvPaidHour.text = "${AmountUtil.getRoundUp(paidHour,1)}小时"

        var totalAmount = AmountUtil.addCommaDots(data.data?.releaseInfo?.totalAmount)
        var unitPrice = AmountUtil.addCommaDots(data.data?.releaseInfo?.price)
        if (data.data?.releaseInfo?.payrollMethod == 1) {
            mTvTotalRemuneration.text = "${totalAmount}元(${unitPrice}元/小时)"
        } else if (data.data?.releaseInfo?.payrollMethod == 2) {
            mTvTotalRemuneration.text = "${totalAmount}元(${unitPrice}元/单)"
        }

        var workProvince = data.data?.releaseInfo?.workProvince ?: ""
        var workCity = data.data?.releaseInfo?.workCity ?: ""
        var workDistrict = data.data?.releaseInfo?.workDistrict ?: ""
        var address = data.data?.releaseInfo?.address ?: ""
        var workAddr = workProvince + workCity + workDistrict + address

        if (isAtHome && TextUtils.isEmpty(address)) {
            mTvWorkAddr.text = "线上"
        } else {
            mTvWorkAddr.text = workAddr
        }

        var workPicsCount = data.data?.releaseInfo?.pics?.size ?: 0
        if (workPicsCount > 0) {
            tv_work_pic.visibility = View.VISIBLE
            mRvWorksPic.visibility = View.VISIBLE
            line_work_pic.visibility = View.VISIBLE
        } else {
            tv_work_pic.visibility = View.GONE
            mRvWorksPic.visibility = View.GONE
            line_work_pic.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data.data?.releaseInfo?.workDescription)) {
            tv_work_description.visibility = View.GONE
            mTvWorkDescription.visibility = View.GONE
        } else {
            tv_work_description.visibility = View.VISIBLE
            mTvWorkDescription.visibility = View.VISIBLE
        }

        mJobWorkPicAdapter?.clear()
        mJobWorkPicAdapter?.add(getWorkPics(data.data?.releaseInfo?.pics))
        mJobWorkPicAdapter?.notifyDataSetChanged()

        mTvWorkDescription.text = data.data?.releaseInfo?.workDescription

        mTvJobOrderId.text = "工单号：${data.data?.orderInfo?.jobOrderId}"
        mTvSignUpTime.text = "报名时间：${data.data?.orderInfo?.signupTime}"
        mTvCancelSignUpTime.text = "取消时间：${data.data?.orderInfo?.cancelSignupTime}"
        mTvEmployTime.text = "录取时间：${data.data?.orderInfo?.employmentTime}"

        var finishType = data?.data?.orderInfo?.finishType
        var status = data?.data?.orderInfo?.status

        when (status) {
            1 -> {//已报名
                //单号、报名时间
                mTvCancelSignUpTime.visibility = View.GONE
                mTvEmployTime.visibility = View.GONE
                mTvFinishTime.visibility = View.GONE
            }
            2 -> {//已取消
                //单号、报名时间、取消时间
                mTvEmployTime.visibility = View.GONE
                mTvFinishTime.visibility = View.GONE
            }
            3 -> {//已雇用
                //单号、报名时间、雇佣时间
                mTvCancelSignUpTime.visibility = View.GONE
                mTvFinishTime.visibility = View.GONE
            }
            4,5 -> {//4待评价;5已结束
                mTvCancelSignUpTime.visibility = View.GONE
                if (finishType == 3) {
                    //单号、报名时间、雇佣时间、完成时间
                    mTvFinishTime.text = "完成时间：${data.data?.orderInfo?.finishTime}"
                } else {
                    //单号、报名时间、雇佣时间、解约时间
                    mTvFinishTime.text = "解约时间：${data.data?.orderInfo?.finishTime}"
                }
            }
        }

    }

    private fun setSpannable(identity: Int,companyTxt: String) {
        var tip = ""
        if (identity == 1) {
            tip = "$companyTxt(企业)"
        } else if (identity == 2) {
            tip = "$companyTxt(商户)"
        } else if (identity == 3) {
            tip = "$companyTxt(个人)"
        }

        SpannableUtil.setSpannableUnderline(mTvCompany,tip,tip)
    }

    fun getWorkPics (pics: List<String>?): List<WorkPicInfo>? {
        var datas = ArrayList<WorkPicInfo>()

        if (pics == null) return datas
        if (pics.size == 0) return datas

        for (url in pics) {
            var data = WorkPicInfo()
            data.pic = url
            datas.add(data)
        }
        return datas
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mIvJobPic -> {
                currentWorkImagePositon = position
                ViewImageActivity.intentStart(this,
                    getViewImageUrls(mJobWorkPicAdapter?.getDatas()),
                    position,
                    view?.findViewById(R.id.mIvJobPic),
                    ResUtils.getStringRes(R.string.img_transition_name))
            }
            else -> {

            }
        }
    }

    override fun onRefresh() {
        sendTalentOrderDetailRequest()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvCompany -> {
                NavigationUtils.goHirerDetailActivity(this,
                    mTalentOrderDetailData?.releaseInfo?.employerId)
            }
            R.id.mTvUserName -> {
                mTalentOrderDetailData
                NavigationUtils.goEmployerDetailActivity(this,mTalentOrderDetailData?.releaseInfo)
            }
            R.id.mTvUserId -> {
                val userId = mTalentOrderDetailData?.releaseInfo?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvJobOrderId -> {
                var jobOrderId = mTalentOrderDetailData?.orderInfo?.jobOrderId
                ClipboardUtil.copy(this,Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}