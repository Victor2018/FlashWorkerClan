package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.ResumeDS
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeViewModelFactory
 * Author: Victor
 * Date: 2020/12/19 10:06
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return ResumeVM(ResumeDS())
    }
}