package com.slender.forumbackend.configuration.seed

data class SeedUserIds(
    val admin: Long,
    val alice: Long,
    val bob: Long,
    val carol: Long,
    val dave: Long,
)

data class SeedTagIds(
    val kotlin: Long,
    val frontend: Long,
    val backend: Long,
    val release: Long,
    val debug: Long,
)

data class SeedArticleIds(
    val homeChain: Long,
    val stateFlow: Long,
    val releaseWeek: Long,
    val demoPlaybook: Long,
)

data class SeedCommentIds(
    val homeRoot: Long,
    val homeReply: Long,
    val homeSecond: Long,
    val stateRoot: Long,
    val stateReply: Long,
    val releaseRoot: Long,
    val playbookRoot: Long,
    val playbookSecond: Long,
)
