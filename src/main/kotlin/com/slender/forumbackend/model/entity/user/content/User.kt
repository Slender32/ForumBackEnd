package com.slender.forumbackend.model.entity.user.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.constant.enumeration.user.Gender
import java.time.LocalDateTime

@TableName("users")
data class User(
    @TableId
    val uid: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val avatar: String,
    val gender: Gender,
    val signature: String,
    val status: UserStatus,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
){
    fun toUserData() = UserData(
        uid = uid,
        name = name,
        email = email,
        avatar = avatar,
        gender = gender,
        signature = signature,
        status = status,
        createTime = createTime.timestamp,
    )
}
