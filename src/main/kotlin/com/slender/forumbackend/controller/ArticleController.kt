package com.slender.forumbackend.controller

import com.slender.forumbackend.service.ArticleService
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val articleService: ArticleService
){

}
