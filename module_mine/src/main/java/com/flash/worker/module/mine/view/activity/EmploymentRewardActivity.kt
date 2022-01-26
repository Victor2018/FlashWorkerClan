package com.flash.worker.module.mine.view.activity

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
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmploymentRewardParm
import com.flash.worker.lib.coremodel.data.parm.ReceiveEmploymentRewardParm
import com.flash.worker.lib.coremodel.data.req.EmploymentRewardReq
import com.flash.worker.lib.coremodel.data.req.ReceiveEmploymentRewardReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmploymentRewardVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.EmployerRewardAdapter
import com.flash.worker.module.mine.view.dialog.EmployerRewardReceiveSuccessDialog
import kotlinx.android.synthetic.main.activity_employment_reward.*

class EmploymentRewardActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EmploymentRewardActivity::class.java)
            activity.startActivity(intent)
        }
    }
    
    private val employmentRewardVM: EmploymentRewardVM by viewModels {
        InjectorUtils.provideEmploymentRewardVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerRewardAdapter: EmployerRewardAdapter? = null
    var currentPage: Int = 1
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.activity_employment_reward

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mEmployerRewardAdapter = EmployerRewardAdapter(this,this)
        mRvEmploymentReward.adapter = mEmployerRewardAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmploymentReward.setLoadMoreListener(this)
        
        mIvBack.setOnClickListener(this)
        mTvReceivingInstructions.setOnClickListener(this)
    }
    
    fun initData () {
        sendEmploymentRewardRequest()
    }

    fun subscribeUi () {
        employmentRewardVM.employmentRewardData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmploymentRewardData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentRewardVM.receiveEmploymentRewardData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(MineActions.REFRESH_EMPLOYMENT_REWARD_TIP)
                    showReceiveSuccessDlg(it.value)
                    currentPage = 1
                    mEmployerRewardAdapter?.clear()
                    mEmployerRewardAdapter?.setFooterVisible(false)
                    mEmployerRewardAdapter?.notifyDataSetChanged()

                    mRvEmploymentReward.setHasMore(false)

                    sendEmploymentRewardRequest()

                    UMengEventModule.report(this, MineEvent.receive_employment_reward)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmploymentRewardRequest () {
        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmploymentRewardParm()
        body.pageNum = currentPage

        employmentRewardVM.fetchEmployerReward(token,body)
    }

    fun sendReceiveEmploymentRewardRequest (employerReleaseId: String?) {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = ReceiveEmploymentRewardParm()
        body.employerReleaseId = employerReleaseId

        employmentRewardVM.receiveEmploymentReward(token,body)
    }

    fun showEmploymentRewardData (data: EmploymentRewardReq?) {
        mEmployerRewardAdapter?.showData(data?.data,mTvNoData,mRvEmploymentReward,currentPage)
    }

    fun showReceiveSuccessDlg (data: ReceiveEmploymentRewardReq) {
        var employerRewardReceiveSuccessDialog = EmployerRewardReceiveSuccessDialog(this)
        employerRewardReceiveSuccessDialog.rewardAmount = data.data?.rewardAmount ?: 0.0
        employerRewardReceiveSuccessDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mTvReceivingInstructions -> {
                EmploymentRewardReceiveNotesActivity.intentStart(this)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var employerReleaseId = mEmployerRewardAdapter?.getItem(position)?.employerReleaseId
        sendReceiveEmploymentRewardRequest(employerReleaseId)
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerRewardAdapter?.clear()
        mEmployerRewardAdapter?.setFooterVisible(false)
        mEmployerRewardAdapter?.notifyDataSetChanged()

        mRvEmploymentReward.setHasMore(false)

        sendEmploymentRewardRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmploymentRewardRequest()
    }
}