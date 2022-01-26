package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.event.TalentEvent
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.interfaces.OnEmployerReleaseListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.ComplexAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.EmployerReleaseDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerSendInviteParm
import com.flash.worker.lib.coremodel.data.parm.InviteTalentEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.req.EmployersReq
import com.flash.worker.lib.coremodel.data.req.InviteTalentEmployerReleaseReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerReleaseVM
import com.flash.worker.lib.coremodel.viewmodel.EmployerVM
import com.flash.worker.lib.coremodel.viewmodel.HomeVM
import com.flash.worker.lib.coremodel.viewmodel.JobInviteVM
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.adapter.InviteTalentReleaseAdapter
import kotlinx.android.synthetic.main.activity_invite_talent.*
import kotlinx.android.synthetic.main.act_invite_talent_content.*
import kotlinx.android.synthetic.main.act_invite_talent_content.view.*
import kotlinx.android.synthetic.main.activity_invite_talent.mIvBack
import kotlinx.android.synthetic.main.activity_invite_talent.mSrlRefresh
import kotlinx.android.synthetic.main.invite_talent_menu.view.*

@Route(path = ARouterPath.InviteTalentAct)
class InviteTalentActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener, OnDropDownMenuClickListener,
    OnEmployerReleaseListener {

    companion object {
        fun intentStart(activity: AppCompatActivity,talentReleaseId: String?,talentUserName: String?) {
            var intent = Intent(activity, InviteTalentActivity::class.java)
            intent.putExtra(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            intent.putExtra(Constant.INTENT_DATA_KEY,talentUserName)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerReleaseDialog: EmployerReleaseDialog? = null
    var popupViews: MutableList<View> = ArrayList()
    var inviteMenuView: View? = null
    var inviteContentView: View? = null
    var mComplexAdapter: ComplexAdapter? = null

    var mEmployersReq: EmployersReq? = null
    var mInviteTalentReleaseAdapter: InviteTalentReleaseAdapter? = null

    var talentReleaseId: String? = null
    var talentUserName: String? = null

    var currentPage: Int = 1
    var currentEmployerPosition: Int = 0

    var isFilterMenuInit = false

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    private val employerReleaseVM: EmployerReleaseVM by viewModels {
        InjectorUtils.provideEmployerReleaseVMFactory(this)
    }

    private val jobInviteVM: JobInviteVM by viewModels {
        InjectorUtils.provideJobInviteVMFactory(this)
    }

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_invite_talent

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

        mLoadingDialog = LoadingDialog(this)

        inviteMenuView = layoutInflater.inflate(R.layout.invite_talent_menu, null)

        mComplexAdapter = ComplexAdapter(this,this)
        inviteMenuView?.mRvEmployerFilter?.adapter = mComplexAdapter

        popupViews.clear()
        popupViews.add(inviteMenuView!!)
        inviteContentView = layoutInflater.inflate(R.layout.act_invite_talent_content, null)
        //init dropdownview
        mInviteDropDownMenu.tabMenuView?.removeAllViews()
        mInviteDropDownMenu.popupMenuViews?.removeAllViews()
        mInviteDropDownMenu.containerView?.removeAllViews()
        mInviteDropDownMenu.isFillWidth = true
        mInviteDropDownMenu.isTextViewFillWidth = true
        mInviteDropDownMenu.textViewGravity = Gravity.CENTER_VERTICAL

        mInviteDropDownMenu.mOnDropDownMenuClickListener = this

        mInviteTalentReleaseAdapter = InviteTalentReleaseAdapter(this,this)

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvNewRelease.setOnClickListener(this)
        inviteContentView?.mTvSendInvitation?.setOnClickListener(this)
    }

    fun initFilterMenu () {
        mInviteDropDownMenu.tabMenuView?.removeAllViews()
        mInviteDropDownMenu.popupMenuViews?.removeAllViews()
        mInviteDropDownMenu.containerView?.removeAllViews()

        var name: String? = ""

        var count = mEmployersReq?.data?.size ?: 0
        if (mEmployersReq?.data != null && count > 0) {
            if (currentEmployerPosition >= count) {
                currentEmployerPosition = 0
            }

            var identity = ""
            if (mEmployersReq?.data?.get(currentEmployerPosition)?.identity == 1) {
                identity = "企业"
            } else if (mEmployersReq?.data?.get(currentEmployerPosition)?.identity == 2) {
                identity = "商户"
            } else if (mEmployersReq?.data?.get(currentEmployerPosition)?.identity == 3) {
                identity = "个人"
            }
            name = "${mEmployersReq?.data?.get(currentEmployerPosition)?.name}($identity)"
        }

        var menus = ArrayList<String>()
        menus.add(name ?: "")
        mInviteDropDownMenu.setDropDownMenu(menus, popupViews, inviteContentView)

        val animatorAdapter
                = AlphaAnimatorAdapter(mInviteTalentReleaseAdapter,mRvInviteRelease)
        mRvInviteRelease.adapter = animatorAdapter

        mRvInviteRelease.setLoadMoreListener(this)
    }

    fun initData (intent: Intent?) {
        talentUserName = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        talentReleaseId = intent?.getStringExtra(Constant.TALENT_RELEASE_ID_KEY)

        Loger.e(TAG,"initData-talentUserName = $talentUserName")
        Loger.e(TAG,"initData-talentReleaseId = $talentReleaseId")

        if (!TextUtils.isEmpty(talentReleaseId)) {
            mTvTitle.text = "邀请人才"
        }
        sendInviteTalentEmployerRequest()
    }

    fun subscribeUi() {
        employerVM.inviteTalentEmployerData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showInviteTalentEmployerData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employerReleaseVM.inviteTalentEmployerReleaseData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    UMengEventModule.report(this, TalentEvent.employer_send_invitation)
                    showInviteTalentEmployerReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        jobInviteVM.employerSendInviteData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    InviteTalentSuccessActivity.intentStart(this,talentUserName)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("001050",it.code)) {
                        showInviteErrorTipDlg(it.message)
                        return@Observer
                    }
                    ToastUtils.show(it.message)
                }
            }
        })

        homeVM.homeEmployerDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(IMActions.SEND_JOB_MESSAGE,it.value.data)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        homeVM.taskDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(IMActions.SEND_TASK_MESSAGE,it.value.data)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendInviteTalentEmployerRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employerVM.fetchInviteTalentEmployer(token)
    }

    fun sendInviteTalentEmployerReleaseRequest (employerId: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        if (TextUtils.isEmpty(employerId)) {
            mSrlRefresh.isRefreshing = false
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = InviteTalentEmployerReleaseParm()
        body.pageNum = currentPage
        body.employerId = employerId

        employerReleaseVM.fetchInviteTalentEmployerRelease(token,body)
    }

    fun sendEmployerInviteTalentRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()

        var checkPosition = mInviteTalentReleaseAdapter?.checkPosition ?: 0
        var employerItem = mInviteTalentReleaseAdapter?.getItem(checkPosition)

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerSendInviteParm()
        body.employerReleaseId = employerItem?.releaseId
        body.talentReleaseId = talentReleaseId

        jobInviteVM.employerSendInvite(token,body)
    }

    fun sendHomeEmployerDetailRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var checkPosition = mInviteTalentReleaseAdapter?.checkPosition ?: 0
        var employerItem = mInviteTalentReleaseAdapter?.getItem(checkPosition)

        homeVM.fetchHomeEmployerDetail(token,employerItem?.releaseId)
    }

    fun sendTaskDetailRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var checkPosition = mInviteTalentReleaseAdapter?.checkPosition ?: 0
        var employerItem = mInviteTalentReleaseAdapter?.getItem(checkPosition)
        homeVM.fetchTaskDetail(token,employerItem?.releaseId)
    }

    fun showInviteTalentEmployerData (datas: EmployersReq) {
        mEmployersReq = datas

        initFilterMenu()

        mComplexAdapter?.clear()
        var count: Int = datas.data?.size ?: 0
        for (i in 0 until count) {
            var identity = ""
            if (datas.data?.get(i)?.identity == 1) {
                identity = "企业"
            } else if (datas.data?.get(i)?.identity == 2) {
                identity = "商户"
            } else if (datas.data?.get(i)?.identity == 3) {
                identity = "个人"
            }
            mComplexAdapter?.add("${datas.data?.get(i)?.name}($identity)")
        }
        mComplexAdapter?.notifyDataSetChanged()

        if (datas.data != null && count > 0) {
            line_category_header.visibility = View.VISIBLE
            mInviteDropDownMenu.tabMenuView?.visibility = View.VISIBLE
            line_filter_menu?.visibility = View.VISIBLE


            if (currentEmployerPosition >= count) {
                currentEmployerPosition = 0
            }
            var id = datas.data?.get(currentEmployerPosition)?.id
            sendInviteTalentEmployerReleaseRequest(id)
        } else {
            line_category_header.visibility = View.GONE
            mInviteDropDownMenu.tabMenuView?.visibility = View.GONE
            line_filter_menu?.visibility = View.GONE
            mTvNoData.visibility = View.VISIBLE
            mRvInviteRelease.visibility = View.GONE
            mTvSendInvitation.visibility = View.GONE
            mInviteTalentReleaseAdapter?.clear()
            mInviteTalentReleaseAdapter?.notifyDataSetChanged()
            mRvInviteRelease.setHasMore(false)
        }
    }

    fun showInviteTalentEmployerReleaseData (datas: InviteTalentEmployerReleaseReq) {
        isFilterMenuInit = true
        mInviteTalentReleaseAdapter?.showData(datas.data,mTvNoData,mRvInviteRelease,currentPage)
        if (datas == null) {
            mInviteDropDownMenu.tabMenuView?.visibility = View.GONE
            line_filter_menu?.visibility = View.GONE
            mTvSendInvitation.visibility = View.GONE
            return
        }
        if (datas.data == null) {
            mInviteDropDownMenu.tabMenuView?.visibility = View.GONE
            line_filter_menu?.visibility = View.GONE
            mTvSendInvitation.visibility = View.GONE
            return
        }
        if (datas.data?.list == null) {
            if (currentPage == 1) {
                mInviteDropDownMenu.tabMenuView?.visibility = View.GONE
                line_filter_menu?.visibility = View.GONE
                mTvSendInvitation.visibility = View.GONE
                return
            }
        }
        if (datas.data?.list?.size == 0) {
            if (currentPage == 1) {
                mInviteDropDownMenu.tabMenuView?.visibility = View.GONE
                line_filter_menu?.visibility = View.GONE
                mTvSendInvitation.visibility = View.GONE
                return
            }
        }
        mInviteDropDownMenu.tabMenuView?.visibility = View.VISIBLE
        line_filter_menu?.visibility = View.VISIBLE
        mTvSendInvitation.visibility = View.VISIBLE
    }

    /**
     * 显示不符合录用条件弹窗
     */
    fun showInviteErrorTipDlg (error: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = error
        commonTipDialog.mOkText = "继续浏览"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.show()
    }

    fun showEmployerReleaseDlg() {
        if (mEmployerReleaseDialog == null) {
            mEmployerReleaseDialog = EmployerReleaseDialog(this)
            mEmployerReleaseDialog?.mOnEmployerReleaseListener = this
        }
        mEmployerReleaseDialog?.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvNewRelease -> {
                showEmployerReleaseDlg()
            }
            R.id.mTvSendInvitation -> {
                if (mInviteTalentReleaseAdapter?.checkPosition == -1) {
                    ToastUtils.show("请选择邀请的岗位")
                    return
                }
                //消息中的岗位邀请
                if (TextUtils.isEmpty(talentReleaseId)) {
                    var checkPosition = mInviteTalentReleaseAdapter?.checkPosition ?: 0
                    var taskType = mInviteTalentReleaseAdapter?.getItem(checkPosition)?.taskType ?: 0
                    if (taskType == 1) {
                        sendHomeEmployerDetailRequest()
                    } else if (taskType == 2) {
                        sendTaskDetailRequest()
                    }

                    return
                }
                sendEmployerInviteTalentRequest()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Loger.e(TAG,"onItemClick-position = $position")
        var data = mInviteTalentReleaseAdapter?.getItem(position)
        when (view?.id) {
            R.id.mClComplexCell -> {
                currentEmployerPosition = position
                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()

                mInviteDropDownMenu.setTabText(mComplexAdapter?.getItem(position))
                mInviteDropDownMenu.closeMenu()

                currentPage = 1
                mInviteTalentReleaseAdapter?.checkPosition = -1
                mInviteTalentReleaseAdapter?.clear()
                mInviteTalentReleaseAdapter?.notifyDataSetChanged()
                var count = mEmployersReq?.data?.size ?: 0
                if (count > 0 && position < count) {
                    sendInviteTalentEmployerReleaseRequest(mEmployersReq?.data?.get(position)?.id)
                }
            }
            R.id.mIvCheck -> {
                if (mInviteTalentReleaseAdapter?.checkPosition == position) {
                    mInviteTalentReleaseAdapter?.checkPosition = -1
                } else {
                    mInviteTalentReleaseAdapter?.checkPosition = position
                }
                mInviteTalentReleaseAdapter?.notifyDataSetChanged()
            }
            else -> {
                var taskType = data?.taskType ?: 0
                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(this, data?.releaseId,
                        null,null, JobDetailAction.PREVIEW)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(this,data?.releaseId,
                        null,null, TaskDetailAction.PREVIEW)
                }
            }
        }
    }

    override fun onRefresh() {
        var count = mEmployersReq?.data?.size ?: 0
        if (mEmployersReq?.data != null && count <= 0) {
            sendInviteTalentEmployerRequest()
            return
        }
        currentPage = 1
        mInviteTalentReleaseAdapter?.clear()
        mInviteTalentReleaseAdapter?.setFooterVisible(false)
        mInviteTalentReleaseAdapter?.notifyDataSetChanged()

        mRvInviteRelease.setHasMore(false)

        sendInviteTalentEmployerReleaseRequest(mEmployersReq?.data?.get(currentEmployerPosition)?.id)
    }

    override fun OnLoadMore() {
        currentPage++
        sendInviteTalentEmployerReleaseRequest(mEmployersReq?.data?.get(currentEmployerPosition)?.id)
    }

    override fun OnDropDownMenuClick(position: Int) {
    }

    override fun OnNewJobRelease() {
        NavigationUtils.goHireNewReleaseActivity(this)
    }

    override fun OnNewTaskRelease() {
        NavigationUtils.goTaskNewReleaseActivity(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }


}