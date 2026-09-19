package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.conversation.ChatMessageListData
import com.slender.forumbackend.model.data.conversation.ChatMessageSendData
import com.slender.forumbackend.model.data.conversation.ConversationCreateData
import com.slender.forumbackend.model.data.conversation.ConversationListData
import com.slender.forumbackend.model.request.ChatMessageListRequest
import com.slender.forumbackend.model.request.ChatMessageSinceRequest
import com.slender.forumbackend.model.request.ChatMessageSendRequest
import com.slender.forumbackend.model.request.ConversationCreateRequest
import com.slender.forumbackend.model.request.ConversationListRequest
import com.slender.forumbackend.model.request.ConversationReadRequest
import com.slender.forumbackend.facade.ConversationFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/conversation")
@Tag(name = "Conversation", description = "私信会话API")
class ConversationController(
    private val conversationFacade: ConversationFacade,
) {
    @GetMapping("/list")
    @Operation(summary = "会话列表", description = "返回当前用户参与的会话，包含尚未发送消息的空会话。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: ConversationListRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ConversationListData> {
        return success(conversationFacade.list(userCache.uid, request))
    }

    @PostMapping
    @Operation(summary = "创建或获取会话", description = "需要登录并关注对方。创建空会话或返回已有会话。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1008 不能和自己创建会话"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "403", description = "1504 请先关注对方"),
            ApiResponse(responseCode = "404", description = "1101 对方不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun create(
        @RequestBody
        @Validated
        request: ConversationCreateRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ConversationCreateData> {
        return success(conversationFacade.create(userCache.uid, request))
    }

    @GetMapping("/{conversationId}/message")
    @Operation(summary = "聊天记录", description = "首次返回最新 size 条，后续往上翻更早消息。items 按时间正序。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "403", description = "1009 不是该会话的参与者"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "404", description = "1501 会话不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun listMessages(
        @PathVariable
        @Parameter(description = "会话ID")
        conversationId: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: ChatMessageListRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ChatMessageListData> {
        return success(conversationFacade.listMessages(conversationId, userCache.uid, request))
    }

    @GetMapping("/{conversationId}/message/since")
    @Operation(summary = "增量同步聊天消息", description = "返回 messageId 大于 afterMessageId 的消息，按消息 ID 正序。")
    @SecurityRequirement(name = "bearerAuth")
    fun listMessagesSince(
        @PathVariable
        @Parameter(description = "会话 ID")
        conversationId: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: ChatMessageSinceRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ChatMessageListData> {
        return success(conversationFacade.listMessagesSince(conversationId, userCache.uid, request))
    }

    @PostMapping("/{conversationId}/message")
    @Operation(summary = "发送私信", description = "凭 clientMessageId 幂等。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1008 内容不合法"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "403", description = "1009 不是该会话的参与者"),
            ApiResponse(responseCode = "404", description = "1501 会话不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun send(
        @PathVariable
        @Parameter(description = "会话ID")
        conversationId: Long,

        @RequestBody
        @Validated
        request: ChatMessageSendRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ChatMessageSendData> {
        return success(conversationFacade.send(conversationId, userCache.uid, request))
    }

    @PostMapping("/{conversationId}/read")
    @Operation(summary = "标记会话已读", description = "lastReadMessageId=-1 表示全部已读。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "403", description = "1009 不是该会话的参与者"),
            ApiResponse(responseCode = "404", description = "1501 会话不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun markRead(
        @PathVariable
        @Parameter(description = "会话ID")
        conversationId: Long,

        @RequestBody
        @Validated
        request: ConversationReadRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        conversationFacade.markRead(conversationId, userCache.uid, request)
        return success()
    }
}
