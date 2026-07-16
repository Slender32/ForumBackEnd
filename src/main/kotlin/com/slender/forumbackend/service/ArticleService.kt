package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticleRepository
import com.slender.forumbackend.model.entity.article.content.Article
import org.springframework.stereotype.Service

interface ArticleService : IService<Article>

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
) : ArticleService, ServiceImpl<ArticleMapper, Article>()
