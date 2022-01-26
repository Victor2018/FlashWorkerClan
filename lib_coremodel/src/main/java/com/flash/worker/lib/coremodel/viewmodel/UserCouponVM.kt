package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import com.flash.worker.lib.coremodel.http.interfaces.IUserCouponDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class UserCouponVM(private val dataSource: IUserCouponDS): ViewModel() {

    val guildCouponData = dataSource.guildCouponData
    fun fetchGuildCoupon(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildCoupon(token)
        }
    }

    val checkReceiveCouponData = dataSource.checkReceiveCouponData
    fun checkReceiveCoupon(token: String?,body: CheckReceiveCouponParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkReceiveCoupon(token,body)
        }
    }

    val receiveCouponData = dataSource.receiveCouponData
    fun receiveCoupon(token: String?,body: ReceiveCouponParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.receiveCoupon(token,body)
        }
    }

    val userCouponData = dataSource.userCouponData
    fun fetchUserCoupon(token: String?,body: UserCouponParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUserCoupon(token,body)
        }
    }

    val useCouponData = dataSource.useCouponData
    fun useCoupon(token: String?,body: UseCouponParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.useCoupon(token,body)
        }
    }


}