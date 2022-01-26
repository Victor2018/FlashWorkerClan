package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildApi
 * Author: Victor
 * Date: 2021/4/12 20:11
 * Description: 
 * -----------------------------------------------------------------
 */
object GuildApi {
    const val APPLY = "guild/submitApply"
    const val SEARCH_GUILD = "guild/searchGuildList"
    const val APPLY_RECORD = "guild/guildApplyList"
    const val CHECK_APPLY = "guild/checkSubmitApply"
    const val MY_GUILD = "guild/getMyGuildInfo"
    const val GUILD_DETAIL = "guild/getGuildDetail"
    const val JOIN_GUILD = "guild/joinGuild"
    const val QUIT_GUILD = "guild/quitGuild"
    const val UPDATE_GUILD_AVATAR = "guild/updateGuildHeadpic"
    const val RELEASE_NEWS = "guild/releaseNews"
    const val GUILD_NEWS = "guild/guildNewsList"
    const val DELETE_GUILD_NEWS = "guild/deleteGuildNews"
    const val UPDATE_GUILD_INTRODUCTION = "guild/updateGuildProfile"
    const val UPDATE_GUILD_REGULATION = "guild/updateGuildRules"
    const val GUILD_MEMBER = "guild/guildMemberList"
    const val MEMBER_DETAIL = "guild/getGuildMemberDetail"
    const val REMOVE_MEMBER = "guild/removeMember"
    const val MEMBER_INCOME_RANK = "guild/guildMemberMonthIncomeRankList"
    const val GUILD_INCOME_STATISTICS = "guild/getGuildIncomeStatistics"
    const val MONTH_INCOME_STATISTICS = "guild/guildMonthIncomeStatisticsList"
}