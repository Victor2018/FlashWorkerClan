package com.flash.worker.lib.coremodel.db.repository

import com.flash.worker.lib.coremodel.data.bean.SearchKeywordType
import com.flash.worker.lib.coremodel.db.AppDatabase
import com.flash.worker.lib.coremodel.db.entity.SearchKeywordEntity


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchKeywordRepository
 * Author: Victor
 * Date: 2021/5/8 10:58
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchKeywordRepository constructor(private val db: AppDatabase) {
    val searchKeywordDao = db.searchKeywordDao()

    suspend fun insertSearchKeyword(searchKeywordEntity: SearchKeywordEntity) {
        searchKeywordDao.insertSearchKeyword(searchKeywordEntity)
    }

    suspend fun deleteSearchKeyword(searchKeywordEntity: SearchKeywordEntity) {
        searchKeywordDao.deleteSearchKeyword(searchKeywordEntity)
    }

    suspend fun deleteSearchKeyword(keyword: String, type:Int,userId: String) {
        searchKeywordDao.deleteSearchKeyword(keyword,type,userId)
    }

    suspend fun clearAll(type: Int,userId: String) {
        searchKeywordDao.clearAll(type,userId)
    }

    fun getSearchKeyword(type: Int,userId: String) = searchKeywordDao.getSearchKeyword(type,userId)

    fun getAllSearchKeyword(type: Int,userId: String) = searchKeywordDao.getAllSearchKeyword(type,userId)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: SearchKeywordRepository? = null

        fun getInstance(db: AppDatabase) =
                instance ?: synchronized(this) {
                    instance ?: SearchKeywordRepository(db).also { instance = it }
                }
    }
}