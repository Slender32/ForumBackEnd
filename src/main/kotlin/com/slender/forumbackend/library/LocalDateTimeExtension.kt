package com.slender.forumbackend.library

import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.ZoneOffset.ofHours

fun Long.toLocalDateTime(): LocalDateTime =
    ofEpochMilli(this).atZone(ofHours(8)).toLocalDateTime()

val LocalDateTime.timestamp : Long
    get() = toInstant(ofHours(8)).toEpochMilli()
