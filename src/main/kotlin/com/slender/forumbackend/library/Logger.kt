package com.slender.forumbackend.library

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.jvm.javaClass

fun Any.logger(): Logger = getLogger(javaClass)
