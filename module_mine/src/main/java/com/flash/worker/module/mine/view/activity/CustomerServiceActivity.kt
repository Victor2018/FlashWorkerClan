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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.CustomerServiceReq
import com.flash.worker.lib.coremodel.http.api.HtmlApi
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.SystemHelpVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.CustomerServiceAdapter
import kotlinx.android.synthetic.main.activity_customer_service.*

class CustomerServiceActivity : BaseActivity(), View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, CustomerServiceActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val systemHelpVM: SystemHelpVM by viewModels {
        InjectorUtils.provideSystemHelpVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mCustomerServiceAdapter: CustomerServiceAdapter? = null

    override fun getLayoutResource() = R.layout.activity_customer_service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mCustomerServiceAdapter = CustomerServiceAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mCustomerServiceAdapter,mRvCustomerService)

        mRvCustomerService.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvOperationGuide.setOnClickListener(this)
    }

    fun subscribeUi () {
        systemHelpVM.customerServiceData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCustomerServiceData(it.value)
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

    fun initData () {
        var spannText = "10:00 - 18:00"
        var text = ResUtils.getStringRes(R.string.mine_customer_service_tip)

        SpannableUtil.setSpannableColor(mTvTipContent,ResUtils.getColorRes(R.color.color_E26853),text,spannText)

        sendCustomerServiceRequest()
    }

    fun sendCustomerServiceRequest () {
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        systemHelpVM.fetchCustomerService(token)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun showCustomerServiceData (data: CustomerServiceReq) {
        mCustomerServiceAdapter?.clear()
        mCustomerServiceAdapter?.add(data.data)
        mCustomerServiceAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mTvOperationGuide ->{
                WebActivity.intentStart(this,"常见问题",HtmlApi.OPERATION_GUID)
                UMengEventModule.report(this,MineEvent.view_operation_guide)
            }
        }
    }

    override fun onRefresh() {
        initData()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var userId = mCustomerServiceAdapter?.getItem(position)?.userId
        sendImLoginInfoRequest(userId)
    }
}