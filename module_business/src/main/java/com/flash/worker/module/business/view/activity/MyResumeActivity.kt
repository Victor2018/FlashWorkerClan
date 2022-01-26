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
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.MyResumeAdapter
import kotlinx.android.synthetic.main.activity_my_resume.*

class MyResumeActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    var mMyResumeAdapter: MyResumeAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, MyResumeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_my_resume


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

        mMyResumeAdapter = MyResumeAdapter(this,this)

        val animatorAdapter = AlphaAnimatorAdapter(mMyResumeAdapter,mRvResume)

        mRvResume.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvAddResume.setOnClickListener(this)
    }

    fun initData () {
        sendUserResumeRequest()
    }

    fun subscribeUi() {
        resumeVM.userResumesData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mMyResumeAdapter?.clear()
                    mMyResumeAdapter?.add(it.value.data)
                    mMyResumeAdapter?.notifyDataSetChanged()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.checkTalentBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NewResumeActivity.intentStart(this)
                    } else {
                        TalentBasicActivity.intentStart(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendUserResumeRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        resumeVM.fetchUserResumes(token)
    }

    fun sendCheckTalentBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkTalentBaseInfo(token)
    }

    fun showAddResumeTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您的简历数已达上限可删除后再新增！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvAddResume -> {
                var count = mMyResumeAdapter?.getContentItemCount() ?: 0
                if (count >= 5) {
                    showAddResumeTipDlg()
                    return
                }

                sendCheckTalentBaseInfoRequest()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var resumeCount = mMyResumeAdapter?.getContentItemCount() ?: 0
        ResumeDetailActivity.intentStart(this,mMyResumeAdapter?.getItem(position),resumeCount)
    }

    override fun onRefresh() {
        sendUserResumeRequest()
    }
}