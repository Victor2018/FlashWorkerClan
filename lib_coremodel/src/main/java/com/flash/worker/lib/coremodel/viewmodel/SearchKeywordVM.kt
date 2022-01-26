package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.bean.SearchKeywordType
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.db.entity.SearchKeywordEntity
import com.flash.worker.lib.coremodel.db.repository.SearchKeywordRepository
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchKeywordVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchKeywordVM(
        private val repository: SearchKeywordRepository,
        private val userId: String): ViewModel() {

    val talentSearchKeywordData = repository.getSearchKeyword(SearchKeywordType.TALENT,userId)
    val employerSearchKeywordData = repository.getSearchKeyword(SearchKeywordType.EMPLOYER,userId)

    val talentAllSearchKeywordData = repository.getAllSearchKeyword(SearchKeywordType.TALENT,userId)
    val employerAllSearchKeywordData = repository.getAllSearchKeyword(SearchKeywordType.EMPLOYER,userId)

    fun insertSearchKeyword(data:SearchKeywordEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertSearchKeyword(data)
            }
        }
    }

    fun deleteSearchKeyword(data:SearchKeywordEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteSearchKeyword(data)
            }
        }
    }

    fun deleteSearchKeyword(keyword: String, type:Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteSearchKeyword(keyword,type,userId)
            }
        }
    }

    fun clearAllSearchKeyword(type: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.clearAll(type, userId)
            }
        }
    }


}