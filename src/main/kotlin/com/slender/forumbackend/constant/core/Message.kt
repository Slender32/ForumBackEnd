package com.slender.forumbackend.constant.core

object Message {
    object User {
        const val LOGIN_SUCCESS = "登录成功"
        const val LOGOUT_SUCCESS = "退出登录成功"
    }

    object Exception {
        const val AUTHORITY_ERROR = "权限不足"
        const val BLOCK_ERROR = "用户已被封禁"
        const val EMAIL_OR_PASSWORD_ERROR = "邮箱或密码错误"
        const val HAS_LOGIN_ERROR = "用户已登录"
        const val INTERNAL_ERROR = "服务器内部错误"
        const val LOGIN_ERROR = "登录失败"
        const val LOGIN_EXPIRED_ERROR = "登录已过期"
        const val LOGIN_NOT_EXPIRED_ERROR = "登录未过期"
        const val REQUEST_READ_ERROR = "请求内容读取失败"
        const val REQUEST_CONTENT_ERROR = "请求内容错误"
        const val TOKEN_EXPIRE_ERROR = "令牌已过期"
        const val TOKEN_NOT_FOUND = "令牌不存在"
        const val TOKEN_SIGNATURE_ERROR = "令牌签名错误"
        const val UNKNOWN_ERROR = "认证失败"
    }
}
