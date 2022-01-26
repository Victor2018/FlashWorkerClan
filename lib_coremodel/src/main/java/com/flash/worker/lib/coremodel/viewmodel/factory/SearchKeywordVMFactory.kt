package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.db.repository.SearchKeywordRepository
import com.flash.worker.lib.coremodel.http.datasource.AccountDS
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.SearchKeywordVM

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchKeywordVMFactory
 * Author: Victor
 * Date: 2020/8/14 下午 05:55
 * Description: 
 * -----------------------------------------------------------------
 */

class SearchKeywordVMFactory(private val repository: SearchKeywordRepository,
                             owner: SavedStateRegistryOwner,
                             private val userId: String): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return SearchKeywordVM(repository,userId)
    }
}