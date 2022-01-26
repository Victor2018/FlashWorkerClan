package com.flash.worker.lib.coremodel.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.savedstate.SavedStateRegistryOwner
import com.flash.worker.lib.coremodel.db.AppDatabase
import com.flash.worker.lib.coremodel.db.repository.SearchKeywordRepository
import com.flash.worker.lib.coremodel.viewmodel.factory.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InjectorUtils
 * Author: Victor
 * Date: 2020/8/13 上午 10:35
 * Description: 
 * -----------------------------------------------------------------
 */

object InjectorUtils {

    fun getSearchKeywordRepository(context: Context): SearchKeywordRepository {
        return SearchKeywordRepository.getInstance(
                AppDatabase.getInstance(context.applicationContext))
    }

    fun provideSearchKeywordVMFactory(activity: AppCompatActivity,userId: String): SearchKeywordVMFactory {
        return SearchKeywordVMFactory(getSearchKeywordRepository(activity),activity,userId)
    }

    fun provideAuthVMFactory(owner: SavedStateRegistryOwner): AuthVMFactory {
        return AuthVMFactory(owner)
    }

    fun provideCommentVMFactory(owner: SavedStateRegistryOwner): CommentVMFactory {
        return CommentVMFactory(owner)
    }

    fun provideCommonVMFactory(owner: SavedStateRegistryOwner): CommonVMFactory {
        return CommonVMFactory(owner)
    }

    fun provideEmployerReleaseVMFactory(owner: SavedStateRegistryOwner): EmployerReleaseVMFactory {
        return EmployerReleaseVMFactory(owner)
    }

    fun provideFileVMFactory(owner: SavedStateRegistryOwner): FileVMFactory {
        return FileVMFactory(owner)
    }

    fun provideHomeVMFactory(owner: SavedStateRegistryOwner): HomeVMFactory {
        return HomeVMFactory(owner)
    }

    fun provideOpenUserVMFactory(owner: SavedStateRegistryOwner): OpenUserFactory {
        return OpenUserFactory(owner)
    }

    fun provideUserVMFactory(owner: SavedStateRegistryOwner): UserVMFactory {
        return UserVMFactory(owner)
    }

    fun provideResumeVMFactory(owner: SavedStateRegistryOwner): ResumeVMFactory {
        return ResumeVMFactory(owner)
    }

    fun provideTalentReleaseVMFactory(owner: SavedStateRegistryOwner): TalentReleaseVMFactory {
        return TalentReleaseVMFactory(owner)
    }

    fun providePayVMFactory(owner: SavedStateRegistryOwner): PayVMFactory {
        return PayVMFactory(owner)
    }

    fun provideEmployerVMFactory(owner: SavedStateRegistryOwner): EmployerVMFactory {
        return EmployerVMFactory(owner)
    }

    fun provideCommonViewModelFactory(owner: SavedStateRegistryOwner): CommonVMFactory {
        return CommonVMFactory(owner)
    }

    fun provideEmploymentVMFactory(owner: SavedStateRegistryOwner): EmploymentVMFactory {
        return EmploymentVMFactory(owner)
    }

    fun provideAccountVMFactory(owner: SavedStateRegistryOwner): AccountVMFactory {
        return AccountVMFactory(owner)
    }

    fun provideTalentJobVMFactory(owner: SavedStateRegistryOwner): TalentJobVMFactory {
        return TalentJobVMFactory(owner)
    }

    fun provideEmployerJobVMFactory(owner: SavedStateRegistryOwner): EmployerJobVMFactory {
        return EmployerJobVMFactory(owner)
    }

    fun provideAttendanceVM(owner: SavedStateRegistryOwner): AttendanceVMFactory {
        return AttendanceVMFactory(owner)
    }

    fun provideJobInviteVMFactory(owner: SavedStateRegistryOwner): JobInviteVMFactory {
        return JobInviteVMFactory(owner)
    }

    fun provideCreditScoreVMFactory(owner: SavedStateRegistryOwner): CreditScoreVMFactory {
        return CreditScoreVMFactory(owner)
    }

    fun provideTalentFavReleaseVMFactory(owner: SavedStateRegistryOwner): TalentFavReleaseVMFactory {
        return TalentFavReleaseVMFactory(owner)
    }

    fun provideEmployerFavReleaseVMFactory(owner: SavedStateRegistryOwner): EmployerFavReleaseVMFactory {
        return EmployerFavReleaseVMFactory(owner)
    }

    fun provideDisputeVMFactory(owner: SavedStateRegistryOwner): DisputeVMFactory {
        return DisputeVMFactory(owner)
    }

    fun provideSystemNoticeVMFactory(owner: SavedStateRegistryOwner): SystemNoticeVMFactory {
        return SystemNoticeVMFactory(owner)
    }

    fun provideGuildVMFactory(owner: SavedStateRegistryOwner): GuildVMFactory {
        return GuildVMFactory(owner)
    }

    fun provideGuildRedEnvelopeVMFactory(owner: SavedStateRegistryOwner): GuildRedEnvelopeVMFactory {
        return GuildRedEnvelopeVMFactory(owner)
    }

    fun provideTalentRedEnvelopeVMFactory(owner: SavedStateRegistryOwner): TalentRedEnvelopeVMFactory {
        return TalentRedEnvelopeVMFactory(owner)
    }

    fun provideEmploymentRewardVMFactory(owner: SavedStateRegistryOwner): EmploymentRewardVMFactory {
        return EmploymentRewardVMFactory(owner)
    }

    fun provideUpdateVMFactory(owner: SavedStateRegistryOwner): UpdateVMFactory {
        return UpdateVMFactory(owner)
    }

    fun provideSystemHelpVMFactory(owner: SavedStateRegistryOwner): SystemHelpVMFactory {
        return SystemHelpVMFactory(owner)
    }

    fun provideViolationReportVMFactory(owner: SavedStateRegistryOwner): ViolationReportVMFactory {
        return ViolationReportVMFactory(owner)
    }

    fun provideShareVMFactory(owner: SavedStateRegistryOwner): ShareVMFactory {
        return ShareVMFactory(owner)
    }

    fun provideUserCouponVMFactory(owner: SavedStateRegistryOwner): UserCouponVMFactory {
        return UserCouponVMFactory(owner)
    }

    fun provideTaskVMFactory(owner: SavedStateRegistryOwner): TaskVMFactory {
        return TaskVMFactory(owner)
    }

}