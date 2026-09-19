package com.slender.forumbackend.toolkit

import java.math.BigInteger

class NumericVersion private constructor(
    private val parts: List<BigInteger>
) : Comparable<NumericVersion> {
    override fun compareTo(other: NumericVersion): Int {
        for (index in 0 until maxOf(parts.size, other.parts.size)) {
            val result = (parts.getOrNull(index) ?: BigInteger.ZERO)
                .compareTo(other.parts.getOrNull(index) ?: BigInteger.ZERO)
            if (result != 0) return result
        }
        return 0
    }

    companion object {
        const val PATTERN = "[0-9]+(\\.[0-9]+)*"
        const val MAX_LENGTH = 64
        private val format = Regex(PATTERN)

        fun parse(value: String): NumericVersion? =
            if (value.length <= MAX_LENGTH && format.matches(value)) {
                NumericVersion(value.split('.').map { it.toBigInteger() })
            } else null
    }
}
