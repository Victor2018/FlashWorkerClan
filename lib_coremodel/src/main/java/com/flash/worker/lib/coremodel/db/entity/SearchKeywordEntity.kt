package com.flash.worker.lib.coremodel.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flash.worker.lib.coremodel.data.bean.SearchKeywordType
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchKeywordEntity
 * Author: Victor
 * Date: 2021/5/7 19:45
 * Description: 
 * -----------------------------------------------------------------
 */
@Entity(tableName = "tb_search_keyword")
data class SearchKeywordEntity(
    @PrimaryKey @ColumnInfo(name = "keyword") var keyword: String,
    @ColumnInfo(name = "user_id") var user_id: String,
    @ColumnInfo(name = "type") val type: Int = SearchKeywordType.TALENT,
    @ColumnInfo(name = "create_date") val create_date: Calendar = Calendar.getInstance()
)