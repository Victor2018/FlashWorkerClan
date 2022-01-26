package com.flash.worker.lib.pay.data.ali

import android.text.TextUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayResult
 * Author: Victor
 * Date: 2020/11/30 18:34
 * Description: 
 * -----------------------------------------------------------------
 */
class PayResult(var rawResult: Map<String, String>?) {
    private var resultStatus: String? = null
    private var result: String? = null
    private var memo: String? = null

    init {
        PayResult(rawResult)
    }

    fun PayResult(rawResult: Map<String, String>?) {
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
    }

    override fun toString(): String {
        return ("resultStatus={" + resultStatus + "};memo={" + memo
                + "};result={" + result + "}")
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
}