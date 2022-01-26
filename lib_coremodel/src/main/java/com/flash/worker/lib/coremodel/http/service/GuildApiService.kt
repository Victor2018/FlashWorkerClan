package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.GuildApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface GuildApiService {

    @GET(GuildApi.MY_GUILD)
    suspend fun fetchMyGuildInfo(
            @Header("X-TOKEN") token: String?): NetworkResponse<MyGuildReq, HttpError>

    @POST(GuildApi.APPLY)
    suspend fun applyEstablishGuild(
            @Header("X-TOKEN") token: String?,
            @Body body: ApplyEstablishGuildParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.SEARCH_GUILD)
    suspend fun searchGuild(
            @Header("X-TOKEN") token: String?,
            @Body body: SearchGuildParm?): NetworkResponse<SearchGuildReq, HttpError>

    @POST(GuildApi.APPLY_RECORD)
    suspend fun fetchApplyRecord(
            @Header("X-TOKEN") token: String?,
            @Body body: GuildApplyRecordParm?): NetworkResponse<GuildApplyRecordReq, HttpError>

    @POST(GuildApi.CHECK_APPLY)
    suspend fun checkApply(
            @Header("X-TOKEN") token: String?): NetworkResponse<CheckApplyReq, HttpError>

    @GET(GuildApi.GUILD_DETAIL)
    suspend fun fetchGuildDetail(
            @Header("X-TOKEN") token: String?,
            @Query("guildId") guildId: String?): NetworkResponse<GuildDetailReq, HttpError>

    @POST(GuildApi.JOIN_GUILD)
    suspend fun joinGuild(
            @Header("X-TOKEN") token: String?,
            @Body body: JoinGuildParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.QUIT_GUILD)
    suspend fun quitGuild(
            @Header("X-TOKEN") token: String?,
            @Body body: QuitGuildParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.UPDATE_GUILD_AVATAR)
    suspend fun updateGuildAvatar(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateGuildAvatarParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.RELEASE_NEWS)
    suspend fun releaseNews(
            @Header("X-TOKEN") token: String?,
            @Body body: ReleaseGuildNewsParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.GUILD_NEWS)
    suspend fun fetchGuildNews(
            @Header("X-TOKEN") token: String?,
            @Body body: GuildNewsParm?): NetworkResponse<GuildNewsReq, HttpError>

    @POST(GuildApi.DELETE_GUILD_NEWS)
    suspend fun deleteGuildNews(
            @Header("X-TOKEN") token: String?,
            @Body body: DeleteGuildNewsParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.UPDATE_GUILD_INTRODUCTION)
    suspend fun updateGuildIntroduction(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateGuildIntroductionParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.UPDATE_GUILD_REGULATION)
    suspend fun updateGuildRegulation(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateGuildRegulationParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.GUILD_MEMBER)
    suspend fun fetchGuildMember(
            @Header("X-TOKEN") token: String?,
            @Body body: GuildMemberParm?): NetworkResponse<GuildMemberReq, HttpError>

    @POST(GuildApi.MEMBER_DETAIL)
    suspend fun fetchMemberDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: MemberDetailParm?): NetworkResponse<MemberDetailReq, HttpError>

    @POST(GuildApi.REMOVE_MEMBER)
    suspend fun removeMember(
            @Header("X-TOKEN") token: String?,
            @Body body: RemoveMemberParm?): NetworkResponse<BaseReq, HttpError>

    @POST(GuildApi.MEMBER_INCOME_RANK)
    suspend fun fetchMemberIncomeRank(
            @Header("X-TOKEN") token: String?,
            @Body body: MemberIncomeRankParm?): NetworkResponse<MemberIncomeRankReq, HttpError>

    @GET(GuildApi.GUILD_INCOME_STATISTICS)
    suspend fun fetchGuildIncomeStatistics(
            @Header("X-TOKEN") token: String?,
            @Query("guildId") guildId: String?): NetworkResponse<GuildIncomeStatisticsReq, HttpError>

    @POST(GuildApi.MONTH_INCOME_STATISTICS)
    suspend fun fetchMonthIncomeStatistics(
            @Header("X-TOKEN") token: String?,
            @Body body: MonthIncomeStatisticsParm?): NetworkResponse<MonthIncomeStatisticsReq, HttpError>

}