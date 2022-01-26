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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.MyEmployerAdapter
import kotlinx.android.synthetic.main.activity_my_employer.*

class MyEmployerActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    var mMyEmployerAdapter: MyEmployerAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, MyEmployerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_my_employer


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

        mMyEmployerAdapter = MyEmployerAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mMyEmployerAdapter,mRvEmployer)

        mRvEmployer.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvAddEmployer.setOnClickListener(this)
    }

    fun initData () {
        sendEmployersRequest()
    }

    fun subscribeUi() {
        employerVM.employersData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mMyEmployerAdapter?.clear()
                    mMyEmployerAdapter?.add(it.value.data)
                    mMyEmployerAdapter?.notifyDataSetChanged()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.checkEmployerBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NewEmployerActivity.intentStart(this)
                    } else {
                        EmployerBasicActivity.intentStart(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmployersRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        employerVM.getEmployers(token)
    }

    fun sendCheckEmployerBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkEmployerBaseInfo(token)
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
            mTvAddEmployer -> {
                var count = mMyEmployerAdapter?.getContentItemCount() ?: 0
                if (count >= 50) {
                    ToastUtils.show("您的雇主数已达上限  您可删除后再新增！")
                    return
                }

                sendCheckEmployerBaseInfoRequest()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        EditEmployerActivity.intentStart(this,mMyEmployerAdapter?.getItem(position)?.id)
    }

    override fun onRefresh() {
        sendEmployersRequest()
    }
}