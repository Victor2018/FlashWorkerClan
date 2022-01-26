package com.flash.worker.lib.common.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnCountDownFinishListener
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.AppInitModule
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.push.JPushOpenHelper
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.SplashViewPagerAdapter
import com.flash.worker.lib.common.view.dialog.PrivacyPolicyDialog
import com.flash.worker.lib.common.util.SharedPreferencesUtils
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.netease.nimlib.sdk.NimIntent
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.stub_guide_content.*
import kotlinx.android.synthetic.main.stub_guide_content.view.*
import kotlinx.android.synthetic.main.stub_splash_content.*
import kotlinx.android.synthetic.main.stub_splash_content.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SplashActivity
 * Author: Victor
 * Date: 2020/12/1 12:00
 * Description: 
 * -----------------------------------------------------------------
 */
class SplashActivity: BaseActivity(), View.OnClickListener, ViewPager.OnPageChangeListener,
    OnCountDownTimerListener {

    var guideRootView: View? = null
    var splashRootView: View? = null
    var mSplashViewPagerAdapter: SplashViewPagerAdapter? = null
    var mSmsCountDownTimer: SmsCountDownTimer? = null
    val countDownTime: Long = 4

    var pushData: String? = null
    var imMessages: ArrayList<IMMessage>? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,pushData: String?) {
            var intent = Intent(activity, SplashActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,pushData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initData(intent)
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtil.hideNavigationBar(this)
    }

    fun initialize (hasShowGuide: Boolean) {
        if (hasShowGuide) {
            if (splashRootView == null) {
                splashRootView = stub_splash?.inflate()
                splashRootView?.mTvSkip?.setOnClickListener(this)
            }
        } else {
            mSplashViewPagerAdapter = SplashViewPagerAdapter(this)
            if (guideRootView == null) {
                guideRootView = stub_guide?.inflate()
                guideRootView?.mVpSplash?.adapter = mSplashViewPagerAdapter
                guideRootView?.mVpSplash?.addOnPageChangeListener(this)
                guideRootView?.mTvGo?.setOnClickListener(this)
            }

            val datas = ArrayList<Int>()
            var imgArray = resources.obtainTypedArray(R.array.splash_imgs)
            for (i in 0 until  imgArray.length()) {
                datas.add(imgArray.getResourceId(i,0))
            }
            mSplashViewPagerAdapter?.datas = datas
            mSplashViewPagerAdapter?.notifyDataSetChanged()
        }

    }

    fun initData (intent: Intent?) {
        val imData= intent?.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT)

        Loger.e(TAG,"initData()......imData = $imData")
        if (imData != null) {
            imMessages = imData as ArrayList<IMMessage>
            Loger.e(TAG,"initData()......imMessages = $imMessages")
        }

        pushData = intent?.getStringExtra(Constant.INTENT_DATA_KEY)

        if (App.get().isMainActivityLaunched()) {
            toMain()
            return
        }

        var hasShowGuide = SharedPreferencesUtils.hasShowGuide
        initialize(hasShowGuide)

        var agreePrivacyPolicy = SharedPreferencesUtils.agreePrivacyPolicy
        if (agreePrivacyPolicy) {
            AppInitModule.initThirdSdk(App.get())
            if (hasShowGuide) {
                startTimer()
            }
        } else {
            showPrivacyPolicyDlg()
        }

    }

    fun toMain () {
        Loger.e(TAG,"toMain()......")
        if (isFinishing) return

        //如果已经已登录并且是通过点击通知栏打开app则进入聊天页面，未登录切换到消息页面会导致跳转到登录页面
        var messageCount = imMessages?.size ?: 0
        if (messageCount > 0) {
            if (App.get().hasLogin()) {
                App.get().finishOtherThenSplashAndMainActivity()
                NavigationUtils.goHomeActivity(this,3,pushData)
            }
        } else {
            if (!App.get().isMainActivityLaunched()) {
                NavigationUtils.goHomeActivity(this,0,pushData)
            } else {
                //如果主界面已经打开直接处理jpush跳转
                parseIntentAction()
            }
        }

        finish()
    }

    fun startTimer () {
        mSmsCountDownTimer?.cancel()
        mSmsCountDownTimer = SmsCountDownTimer(
            countDownTime * 1000, 1000, this)
        mSmsCountDownTimer?.start()
    }

    fun showPrivacyPolicyDlg () {
        var privacyPolicyDialog = PrivacyPolicyDialog(this)
        privacyPolicyDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {

            override fun OnDialogOkClick() {
                SharedPreferencesUtils.agreePrivacyPolicy = true
                SharedPreferencesUtils.hasShowGuide = true
                AppInitModule.initThirdSdk(App.get())
            }

            override fun OnDialogCancelClick() {
                finish()
            }
        }
        privacyPolicyDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvGo, R.id.mTvSkip -> {
                toMain()
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                mRbDot1.isChecked = true
                mTvGo.visibility = View.GONE
            }
            1 -> {
                mRbDot2.isChecked = true
                mTvGo.visibility = View.GONE
            }
            2 -> {
                mRbDot3.isChecked = true
                mTvGo.visibility = View.VISIBLE
            }
        }
    }

    override fun onTick(millisUntilFinished: Long) {
        mTvSkip?.text = "跳过\t${millisUntilFinished / 1000}S"
    }

    override fun onFinish() {
        toMain()
    }

    fun parseIntentAction() {
        //处理极光推送跳转
        JPushOpenHelper.parseIntentAction(this,pushData)
        Loger.e(TAG,"initPushData-pushData = $pushData")
    }

    override fun onDestroy() {
        super.onDestroy()
        mSmsCountDownTimer?.onFinish()
        mSmsCountDownTimer = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}
