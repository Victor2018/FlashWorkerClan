package com.flash.worker.module.login.view.widget

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.http.api.HtmlApi
import com.flash.worker.module.login.R
import com.flash.worker.module.login.view.abs.BaseUIConfig
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.mobile.auth.gatewayauth.*
import org.json.JSONException
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomXmlConfig
 * Author: Victor
 * Date: 2020/12/10 9:53
 * Description: 
 * -----------------------------------------------------------------
 */
class CustomXmlConfig(var mActivity: Activity?, var mAuthHelper: PhoneNumberAuthHelper?):
        BaseUIConfig(mActivity,mAuthHelper), AuthUIControlClickListener,View.OnClickListener {
    val TAG = "CustomXmlConfig"

    var mIvBack: ImageView? = null
    var mTvSmsLogin: TextView? = null
    var mTvWechat: TextView? = null

    var mAuthUIConfig: AuthUIConfig? = null

    var isChecked: Boolean = false

    override fun configAuthPage() {
        mAuthHelper?.setUIClickListener(this)

        mAuthHelper?.removeAuthRegisterXmlConfig()
        mAuthHelper?.removeAuthRegisterViewConfig()

        mAuthHelper?.addAuthRegisterXmlConfig(AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.one_key_login_layout, mAbstractPnsViewDelegate())
                .build())

        mAuthHelper?.setAuthUIConfig(getAuthUIConfig())

    }

    fun getAuthUIConfig(): AuthUIConfig {
        var authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND
        }

        var serviceAgreementUrl = HtmlApi.SERVICE_AGREEMENT
        var privacyPolicyUrl = HtmlApi.PRIVACY_POLICY

        mAuthUIConfig = AuthUIConfig.Builder()
                .setAuthPageActIn("anim_activity_enter", "anim_activity_exit")
                .setAuthPageActOut("anim_activity_enter_back", "anim_activity_exit_back")

                .setPrivacyBefore("登录即表示同意")
                .setAppPrivacyTwo("《闪工族服务协议》", serviceAgreementUrl)
                .setAppPrivacyThree("《闪工族隐私政策》", privacyPolicyUrl)
                .setAppPrivacyColor(ResUtils.getColorRes(R.color.color_999999),
                        ResUtils.getColorRes(R.color.color_F4CC28))
                .setPrivacyTextSize(12)
                .setUncheckedImgPath("chk_login_user_agreement_normal")
                .setCheckedImgPath("chk_login_user_agreement_checked")
                .setPrivacyMargin(ResUtils.getDimenPixRes(R.dimen.dp_35))
                .setPrivacyState(false)
                .setCheckboxHidden(false)

                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(false)
                .setSwitchAccHidden(true)

                .setLightColor(true)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarHidden(false)
                .setWebViewStatusBarColor(Color.WHITE)
                .setWebNavColor(Color.WHITE)
                .setWebNavTextColor(ResUtils.getColorRes(R.color.color_060D11))
                .setWebNavTextSize(18)
                .setNavReturnImgPath("ic_web_nav_back")


                //设置手机号码样式
                .setNumberSize(24)
                .setNumberColor(ResUtils.getColorRes(R.color.color_060D11))
                .setNumFieldOffsetY(200)
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPageBackgroundPath("page_background_color")
                .setLogoImgPath("mytel_app_launcher")
                .setScreenOrientation(authPageOrientation)

                //授权页Slogan
                .setSloganTextColor(ResUtils.getColorRes(R.color.color_999999))
                .setSloganTextSize(12)
                .setSloganOffsetY(260)

                //设置按钮样式
                .setLogBtnText("本机号码一键登录")
                .setLogBtnTextSize(22)
                .setLogBtnTextColor(ResUtils.getColorRes(R.color.color_060D11)).setLogoHidden(false)
                .setLogBtnBackgroundPath("bg_one_key_login_btn")
                .setLogBtnOffsetY(300)
                .setLogBtnToastHidden(true)

                .create()

        return mAuthUIConfig!!
    }

    override fun onClick(code: String?, context: Context?, jsonString: String?) {
        try {
            val jsonObj = JSONObject(jsonString)
            when (code) {
                ResultCode.CODE_ERROR_USER_CHECKBOX -> {
                    isChecked = jsonObj.getBoolean("isChecked")

                    configAuthPage()

                    mAuthHelper?.setAuthUIConfig(getAuthUIConfig())
                }
                ResultCode.CODE_ERROR_USER_LOGIN_BTN -> {
                    if (!jsonObj.getBoolean("isChecked")) {
                        ToastUtils.show("请先阅读服务协议")
                    }
                }
                else -> {
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                mAuthHelper?.hideLoginLoading()
                mAuthHelper?.quitLoginPage()
            }
            R.id.mTvOneKeyLogin -> {
                mAuthHelper?.accelerateLoginPage(1000,null)
            }
            R.id.mTvSmsLogin -> {
                NavigationUtils.goCodeLoginActivity(mActivity,false)
                mAuthHelper?.hideLoginLoading()
                mAuthHelper?.quitLoginPage()
            }
            R.id.mTvWechat -> {
                if (!isChecked) {
                    ToastUtils.show("请先阅读服务协议")
                    return
                }
                LiveDataBus.send(LoginActions.GO_WECHAT_LOGIN)
                mAuthHelper?.hideLoginLoading()
                mAuthHelper?.quitLoginPage()
            }
        }
    }

    inner class mAbstractPnsViewDelegate: AbstractPnsViewDelegate() {
        override fun onViewCreated(view: View?) {
            mIvBack = findViewById(R.id.iv_back) as ImageView?
            mTvSmsLogin = findViewById(R.id.mTvSmsLogin) as TextView?
            mTvWechat = findViewById(R.id.mTvWechat) as TextView?

            mIvBack?.setOnClickListener(this@CustomXmlConfig)
            mTvSmsLogin?.setOnClickListener(this@CustomXmlConfig)
            mTvWechat?.setOnClickListener(this@CustomXmlConfig)

            isChecked = false

        }

    }


}