package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IGuildDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IGuildDS {

    val myGuildInfoData: LiveData<HttpResult<MyGuildReq>>
    suspend fun fetchMyGuildInfo(token: String?)

    val applyData: LiveData<HttpResult<BaseReq>>
    suspend fun apply(token: String?,body: ApplyEstablishGuildParm?)

    val searchGuildData: LiveData<HttpResult<SearchGuildReq>>
    suspend fun searchGuild(token: String?,body: SearchGuildParm?)

    val applyRecordData: LiveData<HttpResult<GuildApplyRecordReq>>
    suspend fun fetchApplyRecord(token: String?,body: GuildApplyRecordParm?)

    val checkApplyData: LiveData<HttpResult<CheckApplyReq>>
    suspend fun checkApply(token: String?)

    val guildDetailData: LiveData<HttpResult<GuildDetailReq>>
    suspend fun fetchGuildDetail(token: String?, guildId: String?)

    val joinGuildData: LiveData<HttpResult<BaseReq>>
    suspend fun joinGuild(token: String?, body: JoinGuildParm?)

    val quitGuildData: LiveData<HttpResult<BaseReq>>
    suspend fun quitGuild(token: String?, body: QuitGuildParm?)

    val updateGuildAvatarData: LiveData<HttpResult<BaseReq>>
    suspend fun updateGuildAvatar(token: String?, body: UpdateGuildAvatarParm?)

    val releaseNewsData: LiveData<HttpResult<BaseReq>>
    suspend fun releaseNews(token: String?, body: ReleaseGuildNewsParm?)

    val guildNewsData: LiveData<HttpResult<GuildNewsReq>>
    suspend fun fetchGuildNews(token: String?, body: GuildNewsParm?)

    val deleteGuildNewsData: LiveData<HttpResult<BaseReq>>
    suspend fun deleteGuildNews(token: String?, body: DeleteGuildNewsParm?)

    val updateGuildIntroductionData: LiveData<HttpResult<BaseReq>>
    suspend fun updateGuildIntroduction(token: String?, body: UpdateGuildIntroductionParm?)

    val updateGuildRegulationData: LiveData<HttpResult<BaseReq>>
    suspend fun updateGuildRegulation(token: String?, body: UpdateGuildRegulationParm?)

    val guildMemberData: LiveData<HttpResult<GuildMemberReq>>
    suspend fun fetchGuildMember(token: String?, body: GuildMemberParm?)

    val memberDetailData: LiveData<HttpResult<MemberDetailReq>>
    suspend fun fetchMemberDetail(token: String?, body: MemberDetailParm?)

    val removeMemberData: LiveData<HttpResult<BaseReq>>
    suspend fun removeMember(token: String?, body: RemoveMemberParm?)

    val memberIncomeRankData: LiveData<HttpResult<MemberIncomeRankReq>>
    suspend fun fetchMemberIncomeRank(token: String?, body: MemberIncomeRankParm?)

    val guildIncomeStatisticsData: LiveData<HttpResult<GuildIncomeStatisticsReq>>
    suspend fun fetchGuildIncomeStatistics(token: String?, guildId: String?)

    val monthIncomeStatisticsData: LiveData<HttpResult<MonthIncomeStatisticsReq>>
    suspend fun fetchMonthIncomeStatistics(token: String?, body: MonthIncomeStatisticsParm?)

}