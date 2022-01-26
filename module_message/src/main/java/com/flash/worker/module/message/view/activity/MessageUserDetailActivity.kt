package com.flash.worker.module.message.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ImUserInfo
import com.flash.worker.lib.coremodel.data.req.ImUserInfoReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.message.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.act_message_user_detail_content_header.*
import kotlinx.android.synthetic.main.act_message_user_detail_header.*
import kotlinx.android.synthetic.main.activity_message_user_detail.*

class MessageUserDetailActivity : BaseActivity(), View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,imAccid: String?) {
            var intent = Intent(activity, MessageUserDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,imAccid)
            activity.startActivity(intent)
        }
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    var imAccid: String? = null
    var mImUserInfo: ImUserInfo? = null

    override fun getLayoutResource() = R.layout.activity_message_user_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialzie()
        initData(intent)
    }

    fun initialzie () {
        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)

        appbar.addOnOffsetChangedListener(this)
    }

    fun initData (intent: Intent?) {
        imAccid = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendImUserInfoRequest()
    }

    fun subscribeUi () {
        userVM.imUserInfoData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showImUserInfoData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendImUserInfoRequest () {
        mSrlRefresh.isRefreshing = false
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImUserInfo(token,imAccid)
    }

    fun showImUserInfoData(data: ImUserInfoReq) {
        mImUserInfo = data?.data
        ImageUtils.instance.loadImage(this,mCivAvatar,data?.data?.headpic,R.mipmap.ic_avatar)
        if (data?.data?.sex == 0) {
            mIvSex.setImageResource(R.mipmap.ic_mine_female)
        } else if (data?.data?.sex == 1) {
            mIvSex.setImageResource(R.mipmap.ic_mine_male)
        } else {
            mIvSex.setImageResource(0)
        }
        mTvUserName.text = data?.data?.username
        mTvUserId.text = "ID:${data?.data?.userId}"
        mTvTalentCreditScore.text = "信用分: ${data?.data?.talentCreditScore}"
        mTvEmployerCreditScore.text = "信用分: ${data?.data?.employerCreditScore}"
        if (data?.data?.sex == 0) {
            mTvSex.text = "女"
            mTvSex.visibility = View.VISIBLE
            line_sex.visibility = View.VISIBLE
        } else if (data?.data?.sex == 1) {
            mTvSex.text = "男"
            mTvSex.visibility = View.VISIBLE
            line_sex.visibility = View.VISIBLE
        } else {
            mTvSex.text = "其他"
            mTvSex.visibility = View.GONE
            line_sex.visibility = View.GONE
        }

        var age = data?.data?.age ?: 0
        if (age > 0) {
            mTvAge.text = "${age}岁"
            mTvAge.visibility = View.VISIBLE
            line_age.visibility = View.VISIBLE
        } else {
            mTvAge.visibility = View.GONE
            line_age.visibility = View.GONE
        }

        if (data?.data?.identity == 1) {//职场人士
            mTvIdentity.text = ""
            mTvIdentity.visibility = View.GONE
            line_identity.visibility = View.GONE
        } else if (data?.data?.identity == 2) {//学生
            mTvIdentity.text = "学生"
            mTvIdentity.visibility = View.VISIBLE
            line_identity.visibility = View.VISIBLE
        } else {
            mTvIdentity.text = ""
            mTvIdentity.visibility = View.GONE
            line_identity.visibility = View.GONE
        }

        var height = data?.data?.height ?: 0
        var weight = data?.data?.weight ?: 0
        if (height > 0) {
            mTvHeight.text = "${height}cm"
            mTvHeight.visibility = View.VISIBLE
            line_height.visibility = View.VISIBLE
        } else {
            mTvHeight.visibility = View.GONE
            line_height.visibility = View.GONE
        }
        if (weight > 0) {
            mTvWeight.text = "${weight}kg"
            mTvWeight.visibility = View.VISIBLE
        } else {
            line_height.visibility = View.GONE
            mTvWeight.visibility = View.INVISIBLE
        }

        var liveCity = data?.data?.liveCity ?: ""
        var liveDistrict = data?.data?.liveDistrict ?: ""
        mTvLiveCity.text = liveCity + liveDistrict
        mTvWorkYears.text = data?.data?.workYears
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mTvUserId -> {
                val userId = mImUserInfo?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
        }
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

    override fun onRefresh() {
        sendImUserInfoRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}