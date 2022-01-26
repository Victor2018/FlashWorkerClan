package com.flash.worker.module.mine.view.interfaces

import com.flash.worker.lib.coremodel.data.bean.WithdrawConfirmDetailData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnTransactionPasswordInputListener
 * Author: Victor
 * Date: 2021/2/1 15:58
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnTransactionPasswordInputListener {
    fun OnTransactionPasswordInput (data: WithdrawConfirmDetailData?, tradePassword: String?)
}