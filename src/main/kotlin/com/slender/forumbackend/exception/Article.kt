package com.slender.forumbackend.exception

sealed class ArticleException: RuntimeException()

class ArticleNotFoundException: ArticleException()
