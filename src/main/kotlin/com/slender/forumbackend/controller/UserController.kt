package com.slender.forumbackend.controller

import com.slender.forumbackend.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
){

}
