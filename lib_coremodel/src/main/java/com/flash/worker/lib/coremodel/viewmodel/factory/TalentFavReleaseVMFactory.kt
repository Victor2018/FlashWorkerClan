package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.TalentFavReleaseDS
import com.flash.worker.lib.coremodel.viewmodel.TalentFavReleaseVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavReleaseVMFactory
 * Author: Victor
 * Date: 2020/8/14 下午 05:55
 * Description: 
 * -----------------------------------------------------------------
 */

class TalentFavReleaseVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return TalentFavReleaseVM(TalentFavReleaseDS())
    }
}