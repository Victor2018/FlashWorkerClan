package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IGuildDS
import com.flash.worker.lib.coremodel.http.service.GuildApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildDS: AbsDS(), IGuildDS {

    override val myGuildInfoData = MutableLiveData<HttpResult<MyGuildReq>>()
    override suspend fun fetchMyGuildInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            myGuildInfoData.value = myGuildInfoReq(token)
        }
    }

    override val applyData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun apply(token: String?,body: ApplyEstablishGuildParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            applyData.value = applyReq(token,body)
        }
    }

    override val searchGuildData = MutableLiveData<HttpResult<SearchGuildReq>>()
    override suspend fun searchGuild(token: String?,body: SearchGuildParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            searchGuildData.value = searchGuildReq(token,body)
        }
    }

    override val applyRecordData = MutableLiveData<HttpResult<GuildApplyRecordReq>>()
    override suspend fun fetchApplyRecord(token: String?,body: GuildApplyRecordParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            applyRecordData.value = applyRecordReq(token,body)
        }
    }

    override val checkApplyData = MutableLiveData<HttpResult<CheckApplyReq>>()
    override suspend fun checkApply(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkApplyData.value = checkApplyReq(token)
        }
    }

    override val guildDetailData = MutableLiveData<HttpResult<GuildDetailReq>>()
    override suspend fun fetchGuildDetail(token: String?, guildId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildDetailData.value = guildDetailReq(token,guildId)
        }
    }

    override val joinGuildData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun joinGuild(token: String?, body: JoinGuildParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            joinGuildData.value = joinGuildReq(token,body)
        }
    }

    override val quitGuildData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun quitGuild(token: String?, body: QuitGuildParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            quitGuildData.value = quitGuildReq(token,body)
        }
    }

    override val updateGuildAvatarData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateGuildAvatar(token: String?, body: UpdateGuildAvatarParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateGuildAvatarData.value = updateGuildAvatarReq(token,body)
        }
    }

    override val releaseNewsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun releaseNews(token: String?, body: ReleaseGuildNewsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            releaseNewsData.value = releaseNewsReq(token,body)
        }
    }

    override val guildNewsData = MutableLiveData<HttpResult<GuildNewsReq>>()
    override suspend fun fetchGuildNews(token: String?, body: GuildNewsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildNewsData.value = guildNewsReq(token,body)
        }
    }

    override val deleteGuildNewsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun deleteGuildNews(token: String?, body: DeleteGuildNewsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            deleteGuildNewsData.value = deleteGuildNewsReq(token,body)
        }
    }

    override val updateGuildIntroductionData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateGuildIntroduction(token: String?, body: UpdateGuildIntroductionParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateGuildIntroductionData.value = updateGuildIntroductionReq(token,body)
        }
    }

    override val updateGuildRegulationData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateGuildRegulation(token: String?, body: UpdateGuildRegulationParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateGuildRegulationData.value = updateGuildRegulationReq(token,body)
        }
    }

    override val guildMemberData = MutableLiveData<HttpResult<GuildMemberReq>>()
    override suspend fun fetchGuildMember(token: String?, body: GuildMemberParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildMemberData.value = guildMemberReq(token,body)
        }
    }

    override val memberDetailData = MutableLiveData<HttpResult<MemberDetailReq>>()
    override suspend fun fetchMemberDetail(token: String?, body: MemberDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            memberDetailData.value = memberDetailReq(token,body)
        }
    }

    override val removeMemberData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun removeMember(token: String?, body: RemoveMemberParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            removeMemberData.value = removeMemberReq(token,body)
        }
    }

    override val memberIncomeRankData = MutableLiveData<HttpResult<MemberIncomeRankReq>>()
    override suspend fun fetchMemberIncomeRank(token: String?, body: MemberIncomeRankParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            memberIncomeRankData.value = memberIncomeRankReq(token,body)
        }
    }

    override val guildIncomeStatisticsData = MutableLiveData<HttpResult<GuildIncomeStatisticsReq>>()
    override suspend fun fetchGuildIncomeStatistics(token: String?, guildId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildIncomeStatisticsData.value = guildIncomeStatisticsReq(token,guildId)
        }
    }

    override val monthIncomeStatisticsData = MutableLiveData<HttpResult<MonthIncomeStatisticsReq>>()
    override suspend fun fetchMonthIncomeStatistics(token: String?, body: MonthIncomeStatisticsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            monthIncomeStatisticsData.value = monthIncomeStatisticsReq(token,body)
        }
    }

    private suspend fun <T> myGuildInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchMyGuildInfo(token)) as T
    }

    private suspend fun <T> applyReq(token: String?,body: ApplyEstablishGuildParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .applyEstablishGuild(token,body)) as T
    }

    private suspend fun <T> searchGuildReq(
            token: String?,body: SearchGuildParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                .searchGuild(token,body)) as T
    }

    private suspend fun <T> applyRecordReq(token: String?,body: GuildApplyRecordParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchApplyRecord(token,body)) as T
    }

    private suspend fun <T> checkApplyReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .checkApply(token)) as T
    }

    private suspend fun <T> guildDetailReq(token: String?, guildId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchGuildDetail(token,guildId)) as T
    }

    private suspend fun <T> joinGuildReq(token: String?, body: JoinGuildParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .joinGuild(token,body)) as T
    }

    private suspend fun <T> quitGuildReq(token: String?, body: QuitGuildParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .quitGuild(token,body)) as T
    }

    private suspend fun <T> updateGuildAvatarReq(token: String?, body: UpdateGuildAvatarParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .updateGuildAvatar(token,body)) as T
    }

    private suspend fun <T> releaseNewsReq(token: String?, body: ReleaseGuildNewsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .releaseNews(token,body)) as T
    }

    private suspend fun <T> guildNewsReq(token: String?, body: GuildNewsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchGuildNews(token,body)) as T
    }

    private suspend fun <T> deleteGuildNewsReq(token: String?, body: DeleteGuildNewsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .deleteGuildNews(token,body)) as T
    }

    private suspend fun <T> updateGuildIntroductionReq(token: String?, body: UpdateGuildIntroductionParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .updateGuildIntroduction(token,body)) as T
    }

    private suspend fun <T> updateGuildRegulationReq(token: String?, body: UpdateGuildRegulationParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .updateGuildRegulation(token,body)) as T
    }

    private suspend fun <T> guildMemberReq(token: String?, body: GuildMemberParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchGuildMember(token,body)) as T
    }

    private suspend fun <T> memberDetailReq(token: String?, body: MemberDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchMemberDetail(token,body)) as T
    }

    private suspend fun <T> removeMemberReq(token: String?, body: RemoveMemberParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .removeMember(token,body)) as T
    }

    private suspend fun <T> memberIncomeRankReq(token: String?, body: MemberIncomeRankParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchMemberIncomeRank(token,body)) as T
    }

    private suspend fun <T> guildIncomeStatisticsReq(token: String?, guildId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchGuildIncomeStatistics(token,guildId)) as T
    }

    private suspend fun <T> monthIncomeStatisticsReq(token: String?, body: MonthIncomeStatisticsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildApiService::class.java)
                        .fetchMonthIncomeStatistics(token,body)) as T
    }


}