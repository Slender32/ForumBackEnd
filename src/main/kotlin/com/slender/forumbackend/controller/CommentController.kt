package com.slender.forumbackend.controller

import com.slender.forumbackend.service.CommentService
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentService: CommentService
){

}
