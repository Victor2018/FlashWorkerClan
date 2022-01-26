package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: Result
 * Author: Victor
 * Date: 2020/12/4 17:47
 * Description: 
 * -----------------------------------------------------------------
 */
sealed class HttpResult<out T : Any> {

    class Success<out T : Any>(val value: T) : HttpResult<T>()

    class Error(val code: String?,val message: String?) : HttpResult<Nothing>()
}