package com.slender.forumbackend.model.data.comment

data class CommentReactionData(
    val emoji: String,
    val count: Int,
    val reactors: List<String> = emptyList(),
    val isReact: Boolean = false,
)
