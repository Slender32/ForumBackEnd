package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.common.ReportWriter
import com.slender.forumbackend.exception.ReportSelfException
import com.slender.forumbackend.model.entity.report.ReportTargetType.Article
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import org.springframework.stereotype.Service

@Service
class ArticleReportService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val reportWriter: ReportWriter,
) {
    fun report(articleId: Long, reporterId: Long, request: ReportRequest) {
        val article = articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        if (article.authorId == reporterId) throw ReportSelfException()
        reportWriter.write(reporterId, Article, articleId, request)
    }
}
