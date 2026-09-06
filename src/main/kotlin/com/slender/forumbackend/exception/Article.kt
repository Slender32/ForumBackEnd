package com.slender.forumbackend.exception

sealed class ArticleException(message: String? = null) : RuntimeException(message)

class ArticleNotFoundException(message: String? = null) : ArticleException(message)

class ArticleAlreadyRewardedException: ArticleException()

class MoePointNotEnoughException: ArticleException()

class ArticleSelfRewardException: ArticleException()

class ArticleTagInvalidException: ArticleException()
