package com.flash.worker.module.login.module

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.module.login.interfaces.OnOneKeyLoginListener
import com.flash.worker.module.login.util.AliPhoneAuthutil
import com.flash.worker.module.login.view.abs.BaseUIConfig
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper
import com.mobile.auth.gatewayauth.ResultCode
import com.mobile.auth.gatewayauth.TokenResultListener
import com.mobile.auth.gatewayauth.model.TokenRet
import org.json.JSONObject
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OneKeyLoginHelper
 * Author: Victor
 * Date: 2020/12/7 16:27
 * Description: 阿里手机号码一键登录模块
 * -----------------------------------------------------------------
 */
class OneKeyLoginHelper(var activity: Activity,var listener: OnOneKeyLoginListener): TokenResultListener {
    val TAG = "OneKeyLoginHelper"

    val AUTH_SECRET = "P4oWX9r/DpP8hdcaintNh6uFj4Jvylh+BFYbl3Ddi8a+40FYqSIrPF4ZmODIW/4gkzAGj4nuhRvM04SxZLI0lrP4jiw04/6HIjwvDcg5eo0CR9i6on6eXAJRbXiTr5Vo47imnWvYBMPkTzoPJBv9t4r0Gxd0jOEC13MiklxXvTE+s2Hh9xzjcEeLDHOIhpzszJEHfAjA1Xfa9Cgs9UdOU98Egfg1Ggo3olVkKGU1RR8zp/vmbH2xxOTf2Tlv9jEbnpmqNwHpJaSGV7BJZ9YTXRJBxpyUv8GEq8i1X+1iAWy8hhjEn6N0U1ySMl5O2lUw"

    private var mPhoneNumberAuthHelper: PhoneNumberAuthHelper? = null
    private var mUIConfig: BaseUIConfig? = null

    fun initSdk () {
        Loger.e(TAG,"initSdk()......")
        try {
            mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(activity, this)
            mPhoneNumberAuthHelper?.getReporter()?.setLoggerEnable(true)
            mUIConfig = BaseUIConfig.init(AliPhoneAuthutil.CUSTOM_XML, activity, mPhoneNumberAuthHelper)
            mPhoneNumberAuthHelper?.setAuthSDKInfo(AUTH_SECRET)

//        mPhoneNumberAuthHelper?.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Loger.e(TAG,"onActivityResult()......requestCode = $requestCode")
        Loger.e(TAG,"onActivityResult()......resultCode = $resultCode")
        if (requestCode == 1002) {
            if (resultCode == 1) {
                val result = data?.getStringExtra("result")
                ToastUtils.show("onActivityResult-result = " + result)
                mPhoneNumberAuthHelper?.quitLoginPage()
                listener.OnOneKeyLogin(false,result)
            }
        }
    }

    /**
     * 进入app就需要登录的场景使用
     */
    fun oneKeyLogin() {
        Loger.e(TAG,"oneKeyLogin()......")
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(activity, this)
        mUIConfig?.configAuthPage()
        getLoginToken(5000)
    }

    /**
     * 拉起授权页
     * @param timeout 超时时间
     */
    fun getLoginToken(timeout: Int) {
        Loger.e(TAG,"getLoginToken()......")
        mPhoneNumberAuthHelper?.getLoginToken(activity, timeout)
//        ToastUtils.showShort("正在唤起授权页")
    }


    fun getResultWithToken(token: String?) {
        Thread(){
            val result: String? = getPhoneNumber(token)
            activity.runOnUiThread(Runnable {
                ToastUtils.show("getResultWithToken-result = $result")
                mPhoneNumberAuthHelper?.quitLoginPage()
            })
        }.start()
    }

    /**
     * 开发者自己app的服务端对接阿里号码认证，并提供接口给app调用
     * 1、调用app服务端接口将一键登录token发送过去
     * 2、app服务端拿到token调用阿里号码认证服务端换号接口，获取手机号
     * 3、app服务端拿到手机号帮用户完成注册以及登录的逻辑，返回账户信息给app
     * @return 账户信息
     */
    fun getPhoneNumber(token: String?): String? {
        Log.e(TAG,"getPhoneNumber()......")
        var result = ""
        try {
            //模拟网络请求
            Thread.sleep(500)
            val pJSONObject = JSONObject()
            pJSONObject.put("account", UUID.randomUUID().toString())
            pJSONObject.put("phoneNumber", "***********")
            result = pJSONObject.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun onTokenFailed(s: String?) {
        Log.e(TAG,"onTokenFailed()......")
        var tokenRet: TokenRet? = null
        try {
            tokenRet = TokenRet.fromJson(s)
            Log.e(TAG,"onTokenFailed()......tokenRet?.getCode() = "+ tokenRet?.getCode())
            if (ResultCode.CODE_ERROR_USER_CANCEL == tokenRet?.getCode()) {
                //模拟的是必须登录 否则直接退出app的场景
            } else {
//                ToastUtils.showShort("onTokenFailed-一键登录失败切换到其他登录方式")
                mPhoneNumberAuthHelper?.quitLoginPage()
                listener.OnOneKeyLogin(false,tokenRet?.msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mPhoneNumberAuthHelper?.quitLoginPage()
            listener.OnOneKeyLogin(false,e.localizedMessage)
        }
        mPhoneNumberAuthHelper?.setAuthListener(null)
    }

    override fun onTokenSuccess(s: String?) {
        Log.e(TAG,"onTokenSuccess()......")
        var tokenRet: TokenRet? = null
        try {
            tokenRet = TokenRet.fromJson(s)
            Log.e(TAG,"onTokenSuccess()......tokenRet = " + tokenRet)
            if (ResultCode.CODE_START_AUTHPAGE_SUCCESS == tokenRet.getCode()) {
                Log.i("TAG", "唤起授权页成功：$s")
                listener.OnOneKeyLogin(true,tokenRet.token)
            }
            if (ResultCode.CODE_SUCCESS == tokenRet.code) {
                Log.e(TAG,"onTokenSuccess()......token = " + tokenRet.token)
//                getResultWithToken(tokenRet.token)
                mPhoneNumberAuthHelper?.quitLoginPage()
                mPhoneNumberAuthHelper?.setAuthListener(null)
                listener.OnOneKeyLogin(true,tokenRet.token)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG,"onTokenSuccess()......error = " + e.localizedMessage)
            mPhoneNumberAuthHelper?.quitLoginPage()
            listener.OnOneKeyLogin(false,e.localizedMessage)
        }
    }
    
    fun closeAuthLogin () {
        mPhoneNumberAuthHelper?.quitLoginPage()
    }
}