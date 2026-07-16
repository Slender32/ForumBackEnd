package com.slender.forumbackend.controller

import com.slender.forumbackend.service.TagService
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(
    private val tagService: TagService
){

}
