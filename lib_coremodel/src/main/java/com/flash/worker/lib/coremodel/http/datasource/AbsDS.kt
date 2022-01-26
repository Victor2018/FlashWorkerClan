package com.flash.worker.lib.coremodel.http.datasource

import android.text.TextUtils
import android.util.Log
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsDS
 * Author: Victor
 * Date: 2021/2/4 16:01
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsDS {
    val TAG = javaClass.simpleName

    fun handleRespone (response: NetworkResponse<Any, HttpError>): HttpResult<Any> {
        Log.e(TAG,"handleRespone()......")
        when (response) {
            is NetworkResponse.Success -> {
                Log.e(TAG,"handleRespone()......Success")
                try {
                    val data = response.body as BaseReq

                    if (!TextUtils.equals(data.code?.toUpperCase(),"OK")) {
                        if (TextUtils.equals(data.code,"001020")) {
                            Log.e(TAG,"handleRespone()......token is invalid will to login")
                            LiveDataBus.send(LoginActions.TOKEN_INVALID)
                            return HttpResult.Error(data.code,"")
                        }
                        return HttpResult.Error(data.code,data.message)
                    }

                    return HttpResult.Success(data)
                }  catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG,"handleRespone()......Success-ERROR")
                    return HttpResult.Error(null,e.localizedMessage)
                }
            }
            is NetworkResponse.ApiError -> {
                Log.e(TAG,"handleRespone()......ApiError")
                val message = response.body.statusMessage
                return HttpResult.Error(null,message)
            }
            is NetworkResponse.NetworkError -> {
                Log.e(TAG,"handleRespone()......NetworkError")
                var message = response.error.localizedMessage
                message = "网络错误,请检查网络连接"
                return HttpResult.Error(null,message)
            }
            is NetworkResponse.UnknownError -> {
                Log.e(TAG,"handleRespone()......UnknownError")
                val message = response.error?.localizedMessage
                return HttpResult.Error(null,message)
            }
            is NetworkResponse.ServerError -> {
                Log.e(TAG,"handleRespone()......ServerError")
                var code = response.code
                var error = response.error
                val message = "code = $code,error = $error"
                return HttpResult.Error(null,message)
            }
        }
    }
}