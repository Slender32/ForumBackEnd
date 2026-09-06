package com.slender.forumbackend.exception

sealed class CommentException: RuntimeException()

class CommentNotFoundException: CommentException()

class CommentContentInvalidException: CommentException()

class CommentDepthExceededException: CommentException()

class CommentForbiddenException: CommentException()
