package com.flash.worker.lib.coremodel.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flash.worker.lib.coremodel.data.bean.SearchKeywordType
import com.flash.worker.lib.coremodel.db.entity.SearchKeywordEntity


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchKeywordDao
 * Author: Victor
 * Date: 2021/5/7 19:50
 * Description: 
 * -----------------------------------------------------------------
 */
@Dao
interface SearchKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchKeyword(searchKeywordEntity: SearchKeywordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datas: List<SearchKeywordEntity>)

    @Delete
    suspend fun deleteSearchKeyword(searchKeywordEntity: SearchKeywordEntity)

    @Query("DELETE FROM tb_search_keyword where keyword=:keyword AND type=:type AND user_id = :userId")
    suspend fun deleteSearchKeyword(keyword: String, type:Int,userId: String)

    @Query("DELETE FROM tb_search_keyword where type=:type AND user_id = :userId")
    suspend fun clearAll(type: Int,userId: String)

    @Query("SELECT * FROM tb_search_keyword where type=:type AND user_id = :userId ORDER BY create_date DESC LIMIT 10")
    fun getSearchKeyword(type: Int,userId: String): LiveData<List<SearchKeywordEntity>>

    @Query("SELECT * FROM tb_search_keyword where type=:type AND user_id = :userId")
    fun getAllSearchKeyword(type: Int,userId: String): LiveData<List<SearchKeywordEntity>>
}