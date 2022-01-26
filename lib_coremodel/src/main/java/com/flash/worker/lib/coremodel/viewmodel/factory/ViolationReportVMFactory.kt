package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.ViolationReportDS
import com.flash.worker.lib.coremodel.viewmodel.ViolationReportVM


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationReportVMFactory
 * Author: Victor
 * Date: 2021/7/15 17:49
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationReportVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return ViolationReportVM(ViolationReportDS())
    }
}