package com.slender.forumbackend.exception

sealed class ReportException : RuntimeException()

class ReportDuplicatedException : ReportException()

class ReportReasonInvalidException : ReportException()

class ReportSelfException : ReportException()
