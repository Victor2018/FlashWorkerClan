package com.flash.worker.module.job.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.ClipboardUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.req.EmployerDetailReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.module.job.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_hirer_detail.*
import kotlinx.android.synthetic.main.employer_detail_content.*
import kotlinx.android.synthetic.main.hirer_detail_header.*

@Route(path = ARouterPath.HirerDetailAct)
class HirerDetailActivity : BaseActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,employerId: String?) {
            var intent = Intent(activity, HirerDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,employerId)
            activity.startActivity(intent)
        }
    }

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var employerId: String? = null

    var mEmployerDetailReq: EmployerDetailReq? = null

    override fun getLayoutResource() = R.layout.activity_hirer_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
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

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        appbar.addOnOffsetChangedListener(this)
    }

    fun initData (intent: Intent?) {
        employerId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)

        sendEmployerDetailRequest()
    }

    fun subscribeUi() {
        employmentVM.employerDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendEmployerDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchEmployerDetail(token,employerId)
    }

    fun showEmployerDetailData (data: EmployerDetailReq) {
        mEmployerDetailReq = data

        ImageUtils.instance.loadImage(this,mCivAvatar,data.data?.headpic,R.mipmap.ic_avatar)

        if (data.data?.identity == 1) {
            mTvCompany.text = "${data.data?.name}(企业)"
        } else  if (data.data?.identity == 2) {
            mTvCompany.text = "${data.data?.name}(商户)"
        } else  if (data.data?.identity == 3) {
            mTvCompany.text = "${data.data?.name}(个人)"
        }

        var licenceAuth = data.data?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
            mIvBusinessLicense.visibility = View.VISIBLE
            mTvBusinessLicense.visibility = View.GONE

            if (!TextUtils.isEmpty(data.data?.licencePic)) {
                ImageUtils.instance.loadImage(this,mIvBusinessLicense,data.data?.licencePic)
            }
        } else {
            mIvCompanyVerified.visibility = View.GONE
            mIvBusinessLicense.visibility = View.INVISIBLE
            mTvBusinessLicense.visibility = View.VISIBLE
        }

        mTvUserName.text = data.data?.username
        mTvUserId.text = "ID:" + data.data?.userId
        mTvEmployerCreditScore.text = "信用分: ${data.data?.employerCreditScore}"

        mTvEmployerIntroduction.setText(data.data?.selfDescription)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvUserId -> {
                val userId = mEmployerDetailReq?.data?.userId
                ClipboardUtil.copy(this,"sgz_user_id",userId)
                ToastUtils.show("已复制到剪贴板")
            }
        }
    }

    override fun onRefresh() {
        sendEmployerDetailRequest()
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
}