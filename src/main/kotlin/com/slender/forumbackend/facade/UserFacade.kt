package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.FavoriteListRequest
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.model.request.UserCommentListRequest
import com.slender.forumbackend.model.request.CancelAccountRequest
import com.slender.forumbackend.model.request.RebindEmailRequest
import com.slender.forumbackend.model.request.UpdateAvatarRequest
import com.slender.forumbackend.model.request.UpdatePasswordRequest
import com.slender.forumbackend.model.request.UpdateSignatureRequest
import com.slender.forumbackend.service.user.UserContentQueryService
import com.slender.forumbackend.service.user.UserFollowService
import com.slender.forumbackend.service.user.UserProfileService
import com.slender.forumbackend.service.user.UserAccountService
import com.slender.forumbackend.service.user.UserReportService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFacade(
    private val userProfileService: UserProfileService,
    private val userAccountService: UserAccountService,
    private val userContentQueryService: UserContentQueryService,
    private val userFollowService: UserFollowService,
    private val userReportService: UserReportService,
) {
    fun updateAvatar(uid: Long, request: UpdateAvatarRequest) =
        userAccountService.updateAvatar(uid, request)

    fun updateSignature(uid: Long, request: UpdateSignatureRequest) =
        userAccountService.updateSignature(uid, request)

    fun rebindEmail(uid: Long, request: RebindEmailRequest) =
        userAccountService.rebindEmail(uid, request)

    fun updatePassword(uid: Long, request: UpdatePasswordRequest) =
        userAccountService.updatePassword(uid, request)

    fun cancelAccount(uid: Long, request: CancelAccountRequest) =
        userAccountService.cancelAccount(uid, request)

    fun profile(uid: Long, currentUserId: Long?) =
        userProfileService.profile(uid, currentUserId)

    fun listArticles(
        uid: Long,
        request: ArticleListRequest,
        currentUserId: Long?,
    ) = userContentQueryService.listArticles(uid, request, currentUserId)

    fun listComments(uid: Long, request: UserCommentListRequest) =
        userContentQueryService.listComments(uid, request)

    fun listFavorites(
        uid: Long,
        request: FavoriteListRequest,
        currentUserId: Long,
    ) = userContentQueryService.listFavorites(uid, request, currentUserId)

    @Transactional
    fun toggleFollow(targetUid: Long, currentUserId: Long) =
        userFollowService.toggleFollow(targetUid, currentUserId)

    fun reportUser(targetUid: Long, reporterId: Long, request: ReportRequest) =
        userReportService.report(targetUid, reporterId, request)
}
