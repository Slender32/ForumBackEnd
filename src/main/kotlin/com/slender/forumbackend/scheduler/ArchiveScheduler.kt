package com.slender.forumbackend.scheduler

import com.slender.forumbackend.service.OssArchiveService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ArchiveScheduler(
    private val archive: OssArchiveService,
    @Value("\${forum.archive.retention-days:30}") private val retentionDays: Long,
) {
    @Scheduled(cron = "\${forum.archive.cron:0 0 3 * * *}")
    fun run() {
        archive.run(LocalDateTime.now().minusDays(retentionDays).truncatedTo(ChronoUnit.SECONDS))
    }
}
