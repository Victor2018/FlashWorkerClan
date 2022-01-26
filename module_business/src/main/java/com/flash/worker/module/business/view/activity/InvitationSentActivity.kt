package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerJobInviteParm
import com.flash.worker.lib.coremodel.data.req.EmployerJobInviteReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.JobInviteVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.InvitationSentAdapter
import kotlinx.android.synthetic.main.activity_invitation_sent.*

class InvitationSentActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    var mLoadingDialog: LoadingDialog? = null
    var mInvitationSentAdapter: InvitationSentAdapter? = null

    var currentPage: Int = 1

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, InvitationSentActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val jobInviteVM: JobInviteVM by viewModels {
        InjectorUtils.provideJobInviteVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_invitation_sent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }


    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mInvitationSentAdapter = InvitationSentAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mInvitationSentAdapter,mRvInvitation)

        mRvInvitation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvInvitation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendEmployerJobInviteRequest()
    }

    fun subscribeUi() {
        jobInviteVM.employerJobInviteData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerJobInviteData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendEmployerJobInviteRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerJobInviteParm()
        body.pageNum = currentPage

        jobInviteVM.fetchEmployerJobInvite(token,body)
    }

    fun showEmployerJobInviteData (data: EmployerJobInviteReq?) {
        mInvitationSentAdapter?.showData(data?.data,mTvNoData,mRvInvitation,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var data = mInvitationSentAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.view_employ_bg -> {
                var employerReleaseId = data?.employerReleaseId
                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(this,employerReleaseId,
                        null,null, JobDetailAction.PREVIEW)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(this,employerReleaseId,
                        null,null, TaskDetailAction.PREVIEW)
                }

            }
            else -> {
                var talentReleaseId = data?.talentReleaseId
                NavigationUtils.goTalentDetailActivity(this,
                    talentReleaseId, TalentDetailAction.NORMAL)
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mInvitationSentAdapter?.clear()
        mInvitationSentAdapter?.setFooterVisible(false)
        mInvitationSentAdapter?.notifyDataSetChanged()

        mRvInvitation.setHasMore(false)

        sendEmployerJobInviteRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerJobInviteRequest()
    }
}