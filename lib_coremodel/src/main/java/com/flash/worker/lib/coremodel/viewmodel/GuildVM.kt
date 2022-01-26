package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IGuildDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildVM(private val dataSource: IGuildDS): ViewModel() {

    val myGuildInfoData = dataSource.myGuildInfoData
    fun fetchMyGuildInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchMyGuildInfo(token)
        }
    }

    val applyData = dataSource.applyData
    fun apply(token: String?,body: ApplyEstablishGuildParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.apply(token,body)
        }
    }

    val searchGuildData = dataSource.searchGuildData
    fun searchGuild(token: String?,body: SearchGuildParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.searchGuild(token,body)
        }
    }

    val applyRecordData = dataSource.applyRecordData
    fun fetchApplyRecord(token: String?,body: GuildApplyRecordParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchApplyRecord(token,body)
        }
    }

    val checkApplyData = dataSource.checkApplyData
    fun checkApply(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkApply(token)
        }
    }

    val guildDetailData = dataSource.guildDetailData
    fun fetchGuildDetail(token: String?, guildId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildDetail(token,guildId)
        }
    }

    val joinGuildData = dataSource.joinGuildData
    fun joinGuild(token: String?, body: JoinGuildParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.joinGuild(token,body)
        }
    }

    val quitGuildData = dataSource.quitGuildData
    fun quitGuild(token: String?, body: QuitGuildParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.quitGuild(token,body)
        }
    }

    val updateGuildAvatarData = dataSource.updateGuildAvatarData
    fun updateGuildAvatar(token: String?, body: UpdateGuildAvatarParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateGuildAvatar(token,body)
        }
    }

    val releaseNewsData = dataSource.releaseNewsData
    fun releaseNews(token: String?, body: ReleaseGuildNewsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.releaseNews(token,body)
        }
    }

    val guildNewsData = dataSource.guildNewsData
    fun fetchGuildNews(token: String?, body: GuildNewsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildNews(token,body)
        }
    }

    val deleteGuildNewsData = dataSource.deleteGuildNewsData
    fun deleteGuildNews(token: String?, body: DeleteGuildNewsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.deleteGuildNews(token,body)
        }
    }

    val updateGuildIntroductionData = dataSource.updateGuildIntroductionData
    fun updateGuildIntroduction(token: String?, body: UpdateGuildIntroductionParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateGuildIntroduction(token,body)
        }
    }

    val updateGuildRegulationData = dataSource.updateGuildRegulationData
    fun updateGuildRegulation(token: String?, body: UpdateGuildRegulationParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateGuildRegulation(token,body)
        }
    }

    val guildMemberData = dataSource.guildMemberData
    fun fetchGuildMember(token: String?, body: GuildMemberParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildMember(token,body)
        }
    }

    val memberDetailData = dataSource.memberDetailData
    fun fetchMemberDetail(token: String?, body: MemberDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchMemberDetail(token,body)
        }
    }

    val removeMemberData = dataSource.removeMemberData
    fun removeMember(token: String?, body: RemoveMemberParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.removeMember(token,body)
        }
    }

    val memberIncomeRankData = dataSource.memberIncomeRankData
    fun fetchMemberIncomeRank(token: String?, body: MemberIncomeRankParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchMemberIncomeRank(token,body)
        }
    }

    val guildIncomeStatisticsData = dataSource.guildIncomeStatisticsData
    fun fetchGuildIncomeStatistics(token: String?, guildId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildIncomeStatistics(token,guildId)
        }
    }

    val monthIncomeStatisticsData = dataSource.monthIncomeStatisticsData
    fun fetchMonthIncomeStatistics(token: String?, body: MonthIncomeStatisticsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchMonthIncomeStatistics(token,body)
        }
    }


}