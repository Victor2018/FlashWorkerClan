package com.flash.worker.lib.coremodel.http.adapter

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NetworkResponseAdapter
 * Author: Victor
 * Date: 2020/12/4 17:38
 * Description: interface implementation to let Retrofit know about [NetworkResponseCall]
 * -----------------------------------------------------------------
 */
class NetworkResponseAdapter<S : Any, E : Any>(
    private val successType: Type,
    private val errorBodyConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Call<NetworkResponse<S, E>>> {

    override fun adapt(call: Call<S>): Call<NetworkResponse<S, E>> {
        return NetworkResponseCall(call, errorBodyConverter)
    }

    override fun responseType(): Type = successType
}