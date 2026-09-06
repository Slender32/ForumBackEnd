package com.slender.forumbackend.library

import org.springframework.util.PathMatcher
import com.slender.forumbackend.constant.enumeration.config.TokenPolicy
import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.Required
import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.None

object RequireToken {
    private val policies = LinkedHashMap<String, TokenPolicy>()
    private val methodPolicies = LinkedHashMap<Pair<String, String>, TokenPolicy>()

    fun requireToken(paths: Array<String>) {
        for (path in paths) policies[path] = Required
    }

    fun notRequireToken(paths: Array<String>) {
        for (path in paths) policies[path] = None
    }

    fun methodPolicy(method: String, path: String, policy: TokenPolicy) {
        methodPolicies[method to path] = policy
    }

    fun methodPolicy(method: String, policy: TokenPolicy, paths: Array<String>) {
        for (path in paths) methodPolicies[method to path] = policy
    }

    operator fun get(url: String) = policies[url] == Required

    fun tokenPolicy(url: String, method: String, pathMatcher: PathMatcher): TokenPolicy {
        methodPolicies.entries.firstOrNull { (key, _) ->
            key.first == method.uppercase() && pathMatcher.match(key.second, url)
        }?.let { return it.value }

        return policies[url] ?: policies.entries
            .firstOrNull { (path) -> pathMatcher.match(path, url) }
            ?.value
            ?: Required
    }
}
