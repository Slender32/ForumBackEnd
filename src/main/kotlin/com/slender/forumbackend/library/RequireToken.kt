package com.slender.forumbackend.library

import org.springframework.util.PathMatcher

object RequireToken {
    private val required = HashMap<String, Boolean>()

    fun configure(vararg configs: Pair<String, Boolean>){
        for (pair in configs) required[pair.first] = pair.second
    }

    fun notRequireToken(paths: Array<String>){
        for (path in paths) required[path] = false
    }

    operator fun get(url: String) = required[url] ?: true

    fun requireToken(url: String, pathMatcher: PathMatcher): Boolean =
        required[url] ?: required.entries
            .firstOrNull { (path) -> pathMatcher.match(path, url) }
            ?.value
            ?: true
}
