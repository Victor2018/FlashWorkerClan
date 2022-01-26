package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.AccountDS
import com.flash.worker.lib.coremodel.viewmodel.AccountVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountVMFactory
 * Author: Victor
 * Date: 2020/8/14 下午 05:55
 * Description: 
 * -----------------------------------------------------------------
 */

class AccountVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return AccountVM(AccountDS())
    }
}