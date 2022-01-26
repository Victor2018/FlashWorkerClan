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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentInfo
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentUserInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerWaitCommentUserParm
import com.flash.worker.lib.coremodel.data.req.EmployerWaitCommentUserReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.EmployerWaitCommentUserAdapter
import kotlinx.android.synthetic.main.activity_employer_wait_comment.*
import kotlinx.android.synthetic.main.rv_employer_wait_comment_user_cell.view.*

class EmployerWaitCommentActivity : BaseActivity(),View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    var mEmployerWaitCommentInfo: EmployerWaitCommentInfo? = null
    var currentPage = 1

    var mEmployerWaitCommentUserAdapter: EmployerWaitCommentUserAdapter? = null
    var currentPosition: Int = -1

    val employerJobVM: EmployerJobVM by viewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,employingData: EmployerWaitCommentInfo?) {
            var intent = Intent(activity, EmployerWaitCommentActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_wait_comment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()
        subscribeUi()

        mEmployerWaitCommentUserAdapter = EmployerWaitCommentUserAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerWaitCommentUserAdapter,mRvWaitComment)

        mRvWaitComment.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvWaitComment.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
        mTvCommentAll.setOnClickListener(this)
        mTvMultipleComment.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mEmployerWaitCommentInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as EmployerWaitCommentInfo?

        var identity = ""
        if (mEmployerWaitCommentInfo?.identity == 1) {
            identity = "企业"
        } else if (mEmployerWaitCommentInfo?.identity == 2) {
            identity = "商户"
        } else if (mEmployerWaitCommentInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mEmployerWaitCommentInfo?.employerName}($identity)"

        var licenceAuth = mEmployerWaitCommentInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        var taskType = mEmployerWaitCommentInfo?.taskType ?: 0
        if (taskType == 1) {
            mTvTitle.text = mEmployerWaitCommentInfo?.title
            mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerWaitCommentInfo?.settlementAmount)
            if (mEmployerWaitCommentInfo?.settlementMethod == 1) {
                tv_daily_salary.text = "元/日/人"
            } else if (mEmployerWaitCommentInfo?.settlementMethod == 2) {
                tv_daily_salary.text = "元/周/人"
            } else if (mEmployerWaitCommentInfo?.settlementMethod == 3) {
                tv_daily_salary.text = "元/单/人"
            }
        } else if (taskType == 2) {
            mTvTitle.text = "${mEmployerWaitCommentInfo?.title}(${mEmployerWaitCommentInfo?.taskQty}件)"
            mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerWaitCommentInfo?.price)
            tv_daily_salary.text = "元/件/人"
        }

        sendEmployerWaitCommentUserRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.REFRESH_E_WAIT_COMMENT_USER)
            .observe(this, Observer {
                currentPage = 1
                mEmployerWaitCommentUserAdapter?.checkMap?.clear()
                mTvCheckCount.text = "(0/5)"
                mTvMultipleComment.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))

                mEmployerWaitCommentUserAdapter?.clear()
                mEmployerWaitCommentUserAdapter?.setFooterVisible(false)
                mEmployerWaitCommentUserAdapter?.notifyDataSetChanged()

                mRvWaitComment.setHasMore(false)

                sendEmployerWaitCommentUserRequest()
            })
    }

    fun subscribeUi() {
        employerJobVM.employerWaitCommentUserData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerWaitCommentUserData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmployerWaitCommentUserRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerWaitCommentUserParm()
        body.pageNum = currentPage
        body.employerReleaseId = mEmployerWaitCommentInfo?.employerReleaseId

        employerJobVM.fetchEmployerWaitCommentUser(token,body)
    }

    fun showEmployerWaitCommentUserData (datas: EmployerWaitCommentUserReq) {
        mEmployerWaitCommentUserAdapter?.showData(datas.data,mTvNoData,mRvWaitComment,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvCommentAll -> {
                if (mEmployerWaitCommentUserAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有待评价的人才")
                    return
                }
                EmployerEvaluationActivity.intentStart(this,mEmployerWaitCommentInfo,null)
            }
            R.id.mTvMultipleComment -> {
                if (mEmployerWaitCommentUserAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有待评价的人才")
                    return
                }
                if (mEmployerWaitCommentUserAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择评价的人才")
                    return
                }

                var waitCommentUsers = getWaitCommentUserInfo(false)
                EmployerEvaluationActivity.intentStart(this,mEmployerWaitCommentInfo,waitCommentUsers)
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerWaitCommentUserAdapter?.clear()
        mEmployerWaitCommentUserAdapter?.setFooterVisible(false)
        mEmployerWaitCommentUserAdapter?.notifyDataSetChanged()

        mRvWaitComment.setHasMore(false)

        sendEmployerWaitCommentUserRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerWaitCommentUserRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mChkCheck -> {
                var isChecked = mEmployerWaitCommentUserAdapter?.isItemChecked(mEmployerWaitCommentUserAdapter?.getItem(position)) ?: false
                if (isChecked) {
                    mEmployerWaitCommentUserAdapter?.checkMap?.remove(mEmployerWaitCommentUserAdapter?.getItem(position)?.jobOrderId)
                } else {
                    mEmployerWaitCommentUserAdapter?.checkMap?.put(mEmployerWaitCommentUserAdapter?.getItem(position)?.jobOrderId!!,
                        mEmployerWaitCommentUserAdapter?.getItem(position)!!)
                }
                mEmployerWaitCommentUserAdapter?.notifyDataSetChanged()

                mTvCheckCount.text = "(${mEmployerWaitCommentUserAdapter?.checkMap?.size}/5)"

                if (mEmployerWaitCommentUserAdapter?.checkMap?.size!! > 0) {
                    mTvMultipleComment.setBackgroundColor(ResUtils.getColorRes(R.color.color_FFD424))
                } else {
                    mTvMultipleComment.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mTvJobOrderId -> {
                var jobOrderId = mEmployerWaitCommentUserAdapter?.getItem(position)?.jobOrderId
                ClipboardUtil.copy(this, Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvComment -> {
                var waitCommentUsers = getWaitCommentUserInfo(true)
                EmployerEvaluationActivity.intentStart(this,mEmployerWaitCommentInfo,waitCommentUsers)
            }
            else -> {
                var resumeId =  mEmployerWaitCommentUserAdapter?.getItem(position)?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(this,resumeId)
            }
        }
    }

    fun getWaitCommentUserInfo (isSingleCheck: Boolean): ArrayList<EmployerWaitCommentUserInfo> {

        var waitCommentUsers = ArrayList<EmployerWaitCommentUserInfo>()

        if (isSingleCheck) {
            waitCommentUsers.add(mEmployerWaitCommentUserAdapter?.getItem(currentPosition)!!)
        } else {
            var datas = mEmployerWaitCommentUserAdapter?.checkMap
            datas?.forEach {
                waitCommentUsers.add(it.value)
            }
        }

        return waitCommentUsers
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}