package com.slender.forumbackend.library

import java.time.LocalDateTime
import java.time.ZoneOffset.ofHours

val LocalDateTime.timestamp : Long
    get() = toEpochSecond(ofHours(8)) * 1000