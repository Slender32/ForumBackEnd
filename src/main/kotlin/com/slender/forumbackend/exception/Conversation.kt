package com.slender.forumbackend.exception

sealed class ConversationException : RuntimeException()

class ConversationNotFoundException : ConversationException()

class MessageSendFailedException : ConversationException()

class MessageTargetInvalidException : ConversationException()

class ConversationForbiddenException : ConversationException()
