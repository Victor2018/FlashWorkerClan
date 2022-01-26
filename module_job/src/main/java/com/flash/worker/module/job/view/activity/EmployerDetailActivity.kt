package com.flash.worker.module.job.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.lib.coremodel.data.bean.HomeEmployerDetailData
import com.flash.worker.lib.coremodel.data.bean.TalentOrderReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.TaskDetailData
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.fragment.EmployingFragment
import com.flash.worker.module.job.view.fragment.EvaluationFragment
import kotlinx.android.synthetic.main.act_employer_detail_header.*
import kotlinx.android.synthetic.main.activity_employer_detail.*

@Route(path = ARouterPath.EmployerDetailAct)
class EmployerDetailActivity : BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment>? = null

    var mHomeEmployerDetailData: HomeEmployerDetailData? = null
    var mTaskDetailData: TaskDetailData? = null
    var mTalentOrderReleaseInfo: TalentOrderReleaseInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: HomeEmployerDetailData?,
                          taskDetailData: TaskDetailData?,releaseInfo: TalentOrderReleaseInfo?) {
            var intent = Intent(activity, EmployerDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.TASK_DETAIL_DATA_KEY,taskDetailData)
            intent.putExtra(Constant.RELEASE_DATA_KEY,releaseInfo)
            activity.startActivity(intent)
        }

    }

    override fun getLayoutResource() = R.layout.activity_employer_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        pagerTitles = ResUtils.getStringArrayRes(R.array.job_employer_detail_titles)

        fragmentList = ArrayList()
        fragmentList?.add(EmployingFragment.newInstance())
        fragmentList?.add(EvaluationFragment.newInstance())

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpDetail.setAdapter(mTabPagerAdapter)
        mTabDetail.setupWithViewPager(mVpDetail)

        mIvBack.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mHomeEmployerDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as HomeEmployerDetailData?
        mTaskDetailData = intent?.getSerializableExtra(Constant.TASK_DETAIL_DATA_KEY)
                as TaskDetailData?
        mTalentOrderReleaseInfo = intent?.getSerializableExtra(Constant.RELEASE_DATA_KEY)
                as TalentOrderReleaseInfo?

        if (mHomeEmployerDetailData != null) {
            ImageUtils.instance.loadImage(this,mCivAvatar,mHomeEmployerDetailData?.headpic,R.mipmap.ic_avatar)
            mTvUserName.text = mHomeEmployerDetailData?.username
            mTvUserId.text = "ID:${mHomeEmployerDetailData?.userId}"
            mTvEmployerCreditScore.text = "信用分: ${mHomeEmployerDetailData?.employerCreditScore}"
        }

        if (mTaskDetailData != null) {
            ImageUtils.instance.loadImage(this,mCivAvatar,mTaskDetailData?.headpic,R.mipmap.ic_avatar)
            mTvUserName.text = mTaskDetailData?.username
            mTvUserId.text = "ID:${mTaskDetailData?.userId}"
            mTvEmployerCreditScore.text = "信用分: ${mTaskDetailData?.employerCreditScore}"
        }
        if (mTalentOrderReleaseInfo != null) {
            ImageUtils.instance.loadImage(this,mCivAvatar,mTalentOrderReleaseInfo?.headpic,R.mipmap.ic_avatar)
            mTvUserName.text = mTalentOrderReleaseInfo?.username
            mTvUserId.text = "ID:${mTalentOrderReleaseInfo?.userId}"
            mTvEmployerCreditScore.text = "信用分: ${mTalentOrderReleaseInfo?.employerCreditScore}"
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvShare -> {
            }
            R.id.mTvUserId -> {
                var userId: String? = null
                if (mHomeEmployerDetailData != null) {
                    userId = mHomeEmployerDetailData?.userId
                }
                if (mTaskDetailData != null) {
                    userId = mTaskDetailData?.userId
                }
                if (mTalentOrderReleaseInfo != null) {
                    userId = mTalentOrderReleaseInfo?.userId
                }
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
        }
    }

    fun setPagerTabTitle (count: Int) {
        mTabPagerAdapter?.setPageTitle(0,"雇用中($count)")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}