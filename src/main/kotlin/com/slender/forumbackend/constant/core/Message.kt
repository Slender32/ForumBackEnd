package com.slender.forumbackend.constant.core

object Message {
    object User {
        const val CAPTCHA_SENT = "验证码已发送"
        const val LOGIN_SUCCESS = "登录成功"
        const val LOGOUT_SUCCESS = "退出登录成功"
        const val REGISTER_SUCCESS = "注册成功"
    }

    object Exception {
        const val ACCESS_TOKEN_EXPIRE_ERROR = "访问令牌已过期"
        const val AUTHORITY_ERROR = "权限不足"
        const val BLOCK_ERROR = "用户已被封禁"
        const val CAPTCHA_INVALID_ERROR = "验证码错误或已过期"
        const val EMAIL_OR_PASSWORD_ERROR = "邮箱或密码错误"
        const val EMAIL_REGISTERED_ERROR = "邮箱已注册"
        const val INTERNAL_ERROR = "服务器内部错误"
        const val LOGIN_ERROR = "登录失败"
        const val REFRESH_TOKEN_EXPIRE_ERROR = "刷新令牌已过期"
        const val REQUEST_READ_ERROR = "请求内容读取失败"
        const val REQUEST_CONTENT_ERROR = "请求内容错误"
        const val TOKEN_NOT_FOUND = "令牌不存在"
        const val TOKEN_SIGNATURE_ERROR = "令牌签名错误"
        const val UNKNOWN_ERROR = "认证失败"
        const val USER_NOT_FOUND_ERROR = "用户不存在"
    }
}
