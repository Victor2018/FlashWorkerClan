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
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentJobInviteParm
import com.flash.worker.lib.coremodel.data.req.TalentJobInviteReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.JobInviteVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.ReceivedInvitationAdapter
import kotlinx.android.synthetic.main.activity_received_invitation.*

class ReceivedInvitationActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    var mLoadingDialog: LoadingDialog? = null
    var mReceivedInvitationAdapter: ReceivedInvitationAdapter? = null

    var currentPage: Int = 1

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ReceivedInvitationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val jobInviteVM: JobInviteVM by viewModels {
        InjectorUtils.provideJobInviteVMFactory(this)
    }
    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_received_invitation


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

        mReceivedInvitationAdapter = ReceivedInvitationAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mReceivedInvitationAdapter,mRvInvitation)

        mRvInvitation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvInvitation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendTalentJobInviteRequest()
    }

    fun subscribeUi() {
        jobInviteVM.talentJobInviteData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentJobInviteData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendTalentJobInviteRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentJobInviteParm()
        body.pageNum = currentPage

        jobInviteVM.fetchTalentJobInvite(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun showTalentJobInviteData (data: TalentJobInviteReq) {
        mReceivedInvitationAdapter?.showData(data.data,mTvNoData,mRvInvitation,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var data = mReceivedInvitationAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mTvContactEmployer -> {
                var userId =  data?.employerUserId
                sendImLoginInfoRequest(userId)
            }
            else -> {
                var employerReleaseId = data?.employerReleaseId
                var talentReleaseId = data?.talentReleaseId
                var resumeId =  data?.resumeId

                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(this, employerReleaseId,
                        talentReleaseId, resumeId, JobDetailAction.ACCEPT_INVITATION)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(this, employerReleaseId,
                        talentReleaseId, resumeId, TaskDetailAction.ACCEPT_INVITATION)
                }
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mReceivedInvitationAdapter?.clear()
        mReceivedInvitationAdapter?.setFooterVisible(false)
        mReceivedInvitationAdapter?.notifyDataSetChanged()

        mRvInvitation.setHasMore(false)

        sendTalentJobInviteRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentJobInviteRequest()
    }
}