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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.ViolationUserReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.ViolationReportVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.ViolationUserAdapter
import kotlinx.android.synthetic.main.activity_violation_notice.*


class ViolationNoticeActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ViolationNoticeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val violationReportVM: ViolationReportVM by viewModels {
        InjectorUtils.provideViolationReportVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mViolationUserAdapter: ViolationUserAdapter? = null

    override fun getLayoutResource() = R.layout.activity_violation_notice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mViolationUserAdapter = ViolationUserAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mViolationUserAdapter,mRvViolationNotice)

        mRvViolationNotice.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

    }

    fun initData () {
        sendViolationUserRequest()
    }

    fun subscribeUi () {
        violationReportVM.violationUserData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showViolationUserData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendViolationUserRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        violationReportVM.fetchViolationUser(token)
    }

    fun showViolationUserData (data: ViolationUserReq?) {
        if (data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolationNotice.visibility = View.GONE
            mViolationUserAdapter?.setFooterVisible(false)
            mViolationUserAdapter?.clear()
            mViolationUserAdapter?.notifyDataSetChanged()
            mRvViolationNotice.setHasMore(false)
            return
        }
        if (data.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolationNotice.visibility = View.GONE
            mViolationUserAdapter?.setFooterVisible(false)
            mViolationUserAdapter?.clear()
            mViolationUserAdapter?.notifyDataSetChanged()
            mRvViolationNotice.setHasMore(false)
            return
        }
        if (data.data?.size == 0) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolationNotice.visibility = View.GONE
            mViolationUserAdapter?.setFooterVisible(false)
            mViolationUserAdapter?.clear()
            mViolationUserAdapter?.notifyDataSetChanged()
            mRvViolationNotice.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        mRvViolationNotice.visibility = View.VISIBLE
        
        mViolationUserAdapter?.clear()
        mViolationUserAdapter?.add(data?.data)
        mViolationUserAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        mViolationUserAdapter?.clear()
        mViolationUserAdapter?.notifyDataSetChanged()

        sendViolationUserRequest()
    }


}