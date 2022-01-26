package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
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
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.TalentCommentStatisticsParm
import com.flash.worker.lib.coremodel.data.parm.TalentLastCommentParm
import com.flash.worker.lib.coremodel.data.req.TalentCommentStatisticsReq
import com.flash.worker.lib.coremodel.data.req.TalentLastCommentReq
import com.flash.worker.lib.coremodel.data.req.TalentResumeDetialReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.adapter.HireQualificationAdapter
import com.flash.worker.module.hire.view.adapter.HireWorkExperienceAdapter
import com.flash.worker.module.hire.view.adapter.HireWorkPicAdapter
import com.flash.worker.module.hire.view.adapter.TalentCommentAdapter
import com.google.android.material.appbar.AppBarLayout
import com.library.flowlayout.FlowLayoutManager
import kotlinx.android.synthetic.main.activity_talent_detail.*
import kotlinx.android.synthetic.main.rv_hire_work_pic_cell.view.*
import kotlinx.android.synthetic.main.talent_detail_evaluation.*
import kotlinx.android.synthetic.main.talent_detail_header.*
import kotlinx.android.synthetic.main.talent_detail_resume.*

@Route(path = ARouterPath.TalentResumeDetailAct)
class TalentResumeDetailActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,AppBarLayout.OnOffsetChangedListener, RadioGroup.OnCheckedChangeListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity,resumeId: String?) {
            var intent = Intent(activity, TalentResumeDetailActivity::class.java)
            intent.putExtra(Constant.RESUME_ID_KEY,resumeId)
            activity.startActivity(intent)
        }
    }

    var mHireWorkExperienceAdapter: HireWorkExperienceAdapter? = null
    var mHireQualificationAdapter: HireQualificationAdapter? = null
    var mHireWorkPicAdapter: HireWorkPicAdapter? = null

    var mTalentResumeDetialReq: TalentResumeDetialReq? = null

    var resumeId: String? = null//简历ID
    var mTalentCommentAdapter: TalentCommentAdapter? = null

    var currentPage = 1
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_talent_resume_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)

        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mHireWorkExperienceAdapter = HireWorkExperienceAdapter(this,this)
        mRvWorkExperience.adapter = mHireWorkExperienceAdapter

        var flowLayoutManager = FlowLayoutManager()
        mRvQualification.layoutManager = flowLayoutManager

        mHireQualificationAdapter = HireQualificationAdapter(this,this)
        mRvQualification.adapter = mHireQualificationAdapter

        mHireWorkPicAdapter = HireWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mHireWorkPicAdapter

        mTalentCommentAdapter = TalentCommentAdapter(this,this)
        mRvEvaluation.adapter = mTalentCommentAdapter

        mIvBack.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        mTvAllEvaluation.setOnClickListener(this)

        appbar_layout.addOnOffsetChangedListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder = mRvWorksPic
                                .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvWorkPic)
                    }
                })
    }

    fun initData (intent: Intent?) {
        resumeId = intent?.getStringExtra(Constant.RESUME_ID_KEY)

        sendHomeTalentDetailRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
                .observeForever(this, Observer {
                    currentWorkImagePositon = it as Int
                })
    }

    fun subscribeUi() {
        employmentVM.talentResumeDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.talentCommentStatisticsData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showCommentStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.talentLastCommentData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showLastCommentData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendHomeTalentDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchTalentResumeDetail(token,resumeId)
    }

    fun sendTalentCommentStatisticsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentCommentStatisticsParm()
        body.userId = mTalentResumeDetialReq?.data?.userInfo?.userId

        commentVM.fetchTalentCommentStatistics(token,body)
    }

    fun sendTalentLastCommentRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentLastCommentParm()
        body.userId = mTalentResumeDetialReq?.data?.userInfo?.userId

        commentVM.fetchTalentLastComment(token,body)
    }


    fun showTalentDetailData (data: TalentResumeDetialReq?) {
        mTalentResumeDetialReq = data

        if (data?.data?.resumeInfo == null) {
            showTalentDeleteResumeTip()
            return
        }

        ImageUtils.instance.loadImage(this,mCivAvatar,data?.data?.userInfo?.headpic)
        mTvUserName.text = data?.data?.userInfo?.username
        mTvTalentCreditScore.text = "信用分: ${data?.data?.userInfo?.talentCreditScore}"
        mTvUserId.text = data?.data?.userInfo?.userId
        if (data?.data?.userInfo?.sex == 0) {
            mTvSex.text = "女"
        } else if (data?.data?.userInfo?.sex == 1) {
            mTvSex.text = "男"
        } else {
            mTvSex.text = "其他"
        }
        mTvAge.text = "${data?.data?.userInfo?.age}岁"
        mTvHeight.text = "${data?.data?.userInfo?.height}cm"
        mTvWeight.text = "${data?.data?.userInfo?.weight}kg"

        var height = data?.data?.userInfo?.height ?: 0
        var weight = data?.data?.userInfo?.weight ?: 0

        if (height > 0) {
            line_identity.visibility = View.VISIBLE
            mTvHeight.visibility = View.VISIBLE
        } else {
            line_identity.visibility = View.GONE
            mTvHeight.visibility = View.GONE
        }

        if (weight > 0) {
            line_height.visibility = View.VISIBLE
            mTvWeight.visibility = View.VISIBLE
        } else {
            line_height.visibility = View.GONE
            mTvWeight.visibility = View.GONE
        }

        mTvLiveCity.text = data?.data?.userInfo?.liveCity + data?.data?.userInfo?.liveDistrict
        mTvWorkYears.text = data?.data?.userInfo?.workYears

        mTvEducation.text = data?.data?.resumeInfo?.highestEducation

        if (data?.data?.userInfo?.identity == 1) {//职场人士
            mTvIdentity.visibility = View.GONE
            line_identity.visibility = View.GONE
        } else if (data?.data?.userInfo?.identity == 2) {//学生
            mTvIdentity.visibility = View.VISIBLE
            line_identity.visibility = View.VISIBLE
        }

        var workExperiencesCount = data?.data?.resumeInfo?.resumeWorkExperiences?.size ?: 0
        if (workExperiencesCount > 0) {
            tv_work_experience.visibility = View.VISIBLE
            mRvWorkExperience.visibility = View.VISIBLE
            line_work_experience.visibility = View.VISIBLE
        } else {
            tv_work_experience.visibility = View.GONE
            mRvWorkExperience.visibility = View.GONE
            line_work_experience.visibility = View.GONE
        }

        var certificatesCount = data?.data?.resumeInfo?.resumeCertificates?.size ?: 0
        if (certificatesCount > 0) {
            tv_qualification.visibility = View.VISIBLE
            mRvQualification.visibility = View.VISIBLE
            line_work_qualification.visibility = View.VISIBLE
        } else {
            tv_qualification.visibility = View.GONE
            mRvQualification.visibility = View.GONE
            line_work_qualification.visibility = View.GONE
        }

        var workPicsCount = data?.data?.resumeInfo?.resumeWorkPics?.size ?: 0
        if (workPicsCount > 0) {
            tv_work_pic.visibility = View.VISIBLE
            mRvWorksPic.visibility = View.VISIBLE
            line_work_pic.visibility = View.VISIBLE
        } else {
            tv_work_pic.visibility = View.GONE
            mRvWorksPic.visibility = View.GONE
            line_work_pic.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data?.data?.resumeInfo?.selfDescription)) {
            tv_introduction.visibility = View.GONE
            mTvIntroduction.visibility = View.GONE
        } else {
            tv_introduction.visibility = View.VISIBLE
            mTvIntroduction.visibility = View.VISIBLE
        }

        mHireWorkExperienceAdapter?.clear()
        mHireWorkExperienceAdapter?.add(data?.data?.resumeInfo?.resumeWorkExperiences)
        mHireWorkExperienceAdapter?.notifyDataSetChanged()

        mHireQualificationAdapter?.clear()
        mHireQualificationAdapter?.add(data?.data?.resumeInfo?.resumeCertificates)
        mHireQualificationAdapter?.notifyDataSetChanged()

        mHireWorkPicAdapter?.clear()
        mHireWorkPicAdapter?.add(data?.data?.resumeInfo?.resumeWorkPics)
        mHireWorkPicAdapter?.notifyDataSetChanged()

        mTvIntroduction.setText(data?.data?.resumeInfo?.selfDescription)

        sendTalentCommentStatisticsRequest()
        sendTalentLastCommentRequest()
    }

    fun showTalentDeleteResumeTip () {
        mTvTalentDeleted.visibility = View.VISIBLE
        cl_header.visibility = View.GONE
        cl_resume.visibility = View.GONE
        cl_evaluation.visibility = View.GONE
    }

    fun showCommentStatisticsData (data: TalentCommentStatisticsReq) {
        var totalCommentNum = AmountUtil.getEvaluationCount(data.data?.totalCommentNum ?: 0)
        tv_evaluation.text = "评价($totalCommentNum)"
        mRbVeryGood.text = AmountUtil.getEvaluationCount(data.data?.goodCommentNum ?: 0)
        mRbGeneral.text = AmountUtil.getEvaluationCount(data.data?.generalCommentNum ?: 0)
        mRbVeryBad.text = AmountUtil.getEvaluationCount(data.data?.badCommentNum ?: 0)
    }

    fun showLastCommentData (datas: TalentLastCommentReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mTalentCommentAdapter?.clear()
            mTalentCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mTalentCommentAdapter?.clear()
            mTalentCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data?.size == 0) {
            if (currentPage == 1) {
                mTvNoData.visibility = View.VISIBLE
                mRvEvaluation.visibility = View.GONE
                mTalentCommentAdapter?.clear()
                mTalentCommentAdapter?.notifyDataSetChanged()
                mRvEvaluation.setHasMore(false)
                return
            }
        }
        mTvNoData.visibility = View.GONE
        mRvEvaluation.visibility = View.VISIBLE
        if (currentPage == 1) {
            mTalentCommentAdapter?.clear()
        }
        mTalentCommentAdapter?.add(datas?.data)

        var count = datas?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvEvaluation.setHasMore(false)
            mTalentCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING_END)
        } else {
            mRvEvaluation.setHasMore(true)
            mTalentCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING)
        }
        mTalentCommentAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvUserId -> {
                ClipboardUtil.copy(this,"sgz_user_id",mTvUserId.text)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id. mTvAllEvaluation -> {
                val userId = mTalentResumeDetialReq?.data?.userInfo?.userId
                TalentAllEvaluationActivity.intentStart(this,userId,0)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mIvWorkPic -> {
                currentWorkImagePositon = position
                ViewImageActivity.intentStart(this,
                        getViewImageUrls(mHireWorkPicAdapter?.getDatas()),
                        position,
                        view?.findViewById(R.id.mIvWorkPic),
                        ResUtils.getStringRes(R.string.img_transition_name))
            }
        }
    }

    override fun onRefresh() {
        sendHomeTalentDetailRequest()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val userId = mTalentResumeDetialReq?.data?.userInfo?.userId
        when (checkedId) {
            R.id.mRbVeryGood -> {
                TalentAllEvaluationActivity.intentStart(this,userId,1)
            }
            R.id.mRbGeneral -> {
                TalentAllEvaluationActivity.intentStart(this,userId,2)
            }
            R.id.mRbVeryBad -> {
                TalentAllEvaluationActivity.intentStart(this,userId,3)
            }
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