package com.flash.worker.lib.pay.data.ali

import android.text.TextUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthResult
 * Author: Victor
 * Date: 2020/11/30 18:34
 * Description: 
 * -----------------------------------------------------------------
 */
class AuthResult(rawResult: Map<String, String>?, removeBrackets: Boolean) {
    private var resultStatus: String? = null
    private var result: String? = null
    private var memo: String? = null
    private var resultCode: String? = null
    private var authCode: String? = null
    private var alipayOpenId: String? = null

    init {
        AuthResult(rawResult,removeBrackets)
    }

    fun AuthResult(rawResult: Map<String, String>?, removeBrackets: Boolean) {
        if (rawResult == null) {
            return
        }
        for (key in rawResult.keys) {
            if (TextUtils.equals(key, "resultStatus")) {
                resultStatus = rawResult[key]
            } else if (TextUtils.equals(key, "result")) {
                result = rawResult[key]
            } else if (TextUtils.equals(key, "memo")) {
                memo = rawResult[key]
            }
        }
        val resultValue = result!!.split("&").toTypedArray()
        for (value in resultValue) {
            if (value.startsWith("alipay_open_id")) {
                alipayOpenId = removeBrackets(getValue("alipay_open_id=", value), removeBrackets)
                continue
            }
            if (value.startsWith("auth_code")) {
                authCode = removeBrackets(getValue("auth_code=", value), removeBrackets)
                continue
            }
            if (value.startsWith("result_code")) {
                resultCode = removeBrackets(getValue("result_code=", value), removeBrackets)
                continue
            }
        }
    }

    private fun removeBrackets(str: String, remove: Boolean): String? {
        var str = str
        if (remove) {
            if (!TextUtils.isEmpty(str)) {
                if (str.startsWith("\"")) {
                    str = str.replaceFirst("\"".toRegex(), "")
                }
                if (str.endsWith("\"")) {
                    str = str.substring(0, str.length - 1)
                }
            }
        }
        return str
    }

    override fun toString(): String {
        return "authCode={$authCode}; resultStatus={$resultStatus}; memo={$memo}; result={$result}"
    }

    private fun getValue(header: String, data: String): String {
        return data.substring(header.length, data.length)
    }

    /**
     * @return the resultStatus
     */
    fun getResultStatus(): String? {
        return resultStatus
    }

    /**
     * @return the memo
     */
    fun getMemo(): String? {
        return memo
    }

    /**
     * @return the result
     */
    fun getResult(): String? {
        return result
    }

    /**
     * @return the resultCode
     */
    fun getResultCode(): String? {
        return resultCode
    }

    /**
     * @return the authCode
     */
    fun getAuthCode(): String? {
        return authCode
    }

    /**
     * @return the alipayOpenId
     */
    fun getAlipayOpenId(): String? {
        return alipayOpenId
    }
}