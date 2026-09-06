package com.slender.forumbackend.service

import com.slender.forumbackend.constant.enumeration.website.WebsitePlatform
import com.slender.forumbackend.model.data.website.WebsiteIntroductionData
import com.slender.forumbackend.model.data.website.WebsiteIntroductionItemData
import com.slender.forumbackend.model.data.website.WebsiteReleaseData
import com.slender.forumbackend.model.data.website.WebsiteReleaseItemData
import org.springframework.stereotype.Service

@Service
class WebsiteService {
    fun introduction(): WebsiteIntroductionData {
        // TODO 接入官网介绍配置或官网介绍数据表。
        return WebsiteIntroductionData(items = INTRODUCTION_ITEMS)
    }

    fun releases(): WebsiteReleaseData {
        // TODO 接入客户端版本发布配置或版本管理表。
        return WebsiteReleaseData(items = RELEASE_ITEMS)
    }

    private companion object {
        val INTRODUCTION_ITEMS = listOf(
            WebsiteIntroductionItemData(
                id = 1,
                title = "发现你感兴趣的内容",
                description = "浏览社区中的精彩内容，快速找到与你兴趣相投的话题。",
                imageUrl = "https://i1.hdslb.com/bfs/archive/64af2fc5d243b6f381b92bca6d4395a9519c5757.jpg",
            ),
            WebsiteIntroductionItemData(
                id = 2,
                title = "随时加入交流讨论",
                description = "参与文章评论与社区互动，让每一次交流都自然流畅。",
                imageUrl = "https://i1.hdslb.com/bfs/archive/010b67496563c29fd8cff150092bd4c7f49f8741.jpg",
            ),
            WebsiteIntroductionItemData(
                id = 3,
                title = "分享你的想法与经验",
                description = "通过清晰易用的编辑体验，记录并分享值得被看见的内容。",
                imageUrl = "https://i1.hdslb.com/bfs/archive/fc380e9c81577d21827ad52b5f3d103fc1109968.jpg",
            ),
            WebsiteIntroductionItemData(
                id = 4,
                title = "在不同设备保持连接",
                description = "面向 Windows 与 Android 平台，随时回到你关注的社区。",
                imageUrl = "https://i1.hdslb.com/bfs/archive/63b52d9d314c3dd90c9bf239808dcf1ebf0bbe3c.jpg",
            ),
        )

        val RELEASE_ITEMS = listOf(
            WebsiteReleaseItemData(
                platform = WebsitePlatform.Windows,
                version = "1.0.0",
                sha256 = "8f8c49eacb4fb47e7d4bb24df1c814ef39d90d52bd5eb349af6c84d47a65d55b",
                downloadUrl = "https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-1.0.0.msi",
            ),
            WebsiteReleaseItemData(
                platform = WebsitePlatform.Windows,
                version = "0.9.0",
                sha256 = "c5e83b14d13e69546f6b5ec2369208a0061de152dc537b5c849723616f5f761b",
                downloadUrl = "https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-0.9.0.msi",
            ),
            WebsiteReleaseItemData(
                platform = WebsitePlatform.Windows,
                version = "0.8.0",
                sha256 = "3bd4724329f7aa03c8ba34bf18e6fc0507e085380b09751b63bfaf37b8d87f45",
                downloadUrl = "https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-0.8.0.msi",
            ),
        )
    }
}
