package com.flash.worker.lib.coremodel.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.http.datasource.TaskDS
import com.flash.worker.lib.coremodel.viewmodel.TaskVM


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskVMFactory
 * Author: Victor
 * Date: 2021/12/6 15:35
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskVMFactory(owner: SavedStateRegistryOwner): BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return TaskVM(TaskDS())
    }
}