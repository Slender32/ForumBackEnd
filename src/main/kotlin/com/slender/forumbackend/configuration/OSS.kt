package com.slender.forumbackend.configuration

import com.aliyun.sdk.service.oss2.OSSClient
import com.aliyun.sdk.service.oss2.OSSClient.newBuilder
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OSS(
    @Value($$"${forum.oss.accessKeyId}")
    private val accessKeyId: String,

    @Value($$"${forum.oss.accessKeySecret}")
    private val accessKeySecret: String
) {
    companion object {
        private const val REGION = "cn-shenzhen"
        const val BUCKET_NAME = "ndp-chat-room"
        const val ENDPOINT = "oss-cn-shenzhen.aliyuncs.com"
    }

   @Bean
   fun client(): OSSClient {
       return newBuilder().credentialsProvider(
           StaticCredentialsProvider(accessKeyId, accessKeySecret)
       ).region(REGION).build()
   }
}
