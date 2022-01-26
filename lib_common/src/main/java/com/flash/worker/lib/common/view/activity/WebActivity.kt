package com.flash.worker.lib.common.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Loger
import kotlinx.android.synthetic.main.activity_web.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WebActivity
 * Author: Victor
 * Date: 2020/7/15 上午 11:51
 * Description: 
 * -----------------------------------------------------------------
 */
class WebActivity: BaseActivity(), View.OnClickListener {
    var url: String? = null
    companion object {
        val WEB_TITLE_KEY = "WEB_TITLE_KEY"
        val WEB_URL_KEY = "WEB_URL_KEY"
        val WEB_VIEW_BLACK_KEY = "WEB_VIEW_BLACK_KEY"

        fun  intentStart (activity: Context, title: String?, url: String?) {
            Loger.e("WebActivity", "intentStart()......title = $title")
            Loger.e("WebActivity", "intentStart()......url = $url")
            var intent = Intent(activity, WebActivity::class.java)
            intent.putExtra(WEB_TITLE_KEY, title)
            intent.putExtra(WEB_URL_KEY, url)
            activity.startActivity(intent)
        }
        fun  intentStart (activity: AppCompatActivity, title: String?, url: String?, isBlackBackground: Boolean) {
            var intent = Intent(activity, WebActivity::class.java)
            intent.putExtra(WEB_TITLE_KEY, title)
            intent.putExtra(WEB_URL_KEY, url)
            intent.putExtra(WEB_VIEW_BLACK_KEY, isBlackBackground)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_web
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
    }

    fun initData (){
        val title = intent.getStringExtra(WEB_TITLE_KEY)
        url = intent.getStringExtra(WEB_URL_KEY)
        val isBlack = intent.getBooleanExtra(WEB_VIEW_BLACK_KEY, false)

        Loger.e(TAG,"title = $title")
        Loger.e(TAG,"url = $url")

        mTvTitle.text = title
        mPWeb.setWebViewBackgroundColor(isBlack)
        mPWeb.loadUrl(url!!)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mPWeb.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPWeb.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPWeb.onDestory()
    }
}