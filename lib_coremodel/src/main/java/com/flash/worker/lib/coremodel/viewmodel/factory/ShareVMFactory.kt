package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.ShareDS
import com.flash.worker.lib.coremodel.viewmodel.ShareVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareVMFactory
 * Author: Victor
 * Date: 2020/8/14 下午 05:55
 * Description: 
 * -----------------------------------------------------------------
 */

class ShareVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return ShareVM(ShareDS())
    }
}