package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.EmployerEmployingInfo
import com.flash.worker.lib.coremodel.data.bean.SettlementDateData
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.module.business.view.fragment.*
import kotlinx.android.synthetic.main.activity_employer_task_employing.*

class EmployerTaskEmployingActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, employingData: EmployerEmployingInfo?) {
            var intent = Intent(activity, EmployerTaskEmployingActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }
    }

    private val employerJobVM: EmployerJobVM by viewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()


    var mEmployerEmployingInfo: EmployerEmployingInfo? = null

    override fun getLayoutResource() = R.layout.activity_employer_task_employing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent)
        initialize()
    }

    fun initialize () {
        subscribeUi()

        pagerTitles = ResUtils.getStringArrayRes(R.array.business_employer_task_employing_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(EmployerTaskReceivedFragment.newInstance())//已领取
        fragmentList?.add(EmployerTaskSubmittedFragment.newInstance())//已提交
        fragmentList?.add(EmployerTaskSettledFragment.newInstance())//已结算
        fragmentList?.add(EmployerTaskCancelledFragment.newInstance())//已解约

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpEmploying.adapter = mTabPagerAdapter
        mTabEmploying.setupWithViewPager(mVpEmploying)

        mIvBack.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mEmployerEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as EmployerEmployingInfo?

        var identity = ""
        if (mEmployerEmployingInfo?.identity == 1) {
            identity = "企业"
        } else if (mEmployerEmployingInfo?.identity == 2) {
            identity = "商户"
        } else if (mEmployerEmployingInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mEmployerEmployingInfo?.employerName}(${identity})"

        var licenceAuth = mEmployerEmployingInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        mTvTitle.text = "${mEmployerEmployingInfo?.title}(${mEmployerEmployingInfo?.taskQty}件)"

        mTvPrice.text = AmountUtil.addCommaDots(mEmployerEmployingInfo?.price)

    }

    fun subscribeUi() {
        employerJobVM.employmentNumData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    setPagerTabTitle(0,it.value.data?.receiveNum ?: 0)
                    setPagerTabTitle(1,it.value.data?.submitNum ?: 0)
                    setPagerTabTitle(2,it.value.data?.settledNum ?: 0)
                    setPagerTabTitle(3,it.value.data?.cancelContractNum ?: 0)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmploymentNumRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val employerReleaseId = mEmployerEmployingInfo?.employerReleaseId

        employerJobVM.fetchEmploymentNum(token,employerReleaseId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    fun setPagerTabTitle (position: Int,count: Int) {
        when (position) {
            0 -> {
                mTabPagerAdapter?.setPageTitle(position,"已领取($count)")
            }
            1 -> {
                mTabPagerAdapter?.setPageTitle(position,"已提交($count)")
            }
            2 -> {
                mTabPagerAdapter?.setPageTitle(position,"已结算($count)")
            }
            3 -> {
                mTabPagerAdapter?.setPageTitle(position,"已解约($count)")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}