package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.AuthDS
import com.flash.worker.lib.coremodel.viewmodel.AuthVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthViewModelFactory
 * Author: Victor
 * Date: 2020/8/14 下午 05:55
 * Description: 
 * -----------------------------------------------------------------
 */

class AuthVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return AuthVM(AuthDS())
    }
}